import React, { memo, useEffect, useMemo, useState, useCallback, useRef } from "react";
import { Canvas } from "@react-three/fiber";
import { OBJLoader } from "three/examples/jsm/loaders/OBJLoader";
import { MTLLoader } from "three/examples/jsm/loaders/MTLLoader";
import { OrbitControls } from "@react-three/drei";
import * as THREE from "three";
import useDownloadAndSaveFile from "../../utils/indexDb/useDownloadAndSaveFile";
import { checkStorage } from "../../utils/indexDb/indexDbSetup";
import { useDispatch } from "react-redux";
import { updateMinMaxFor3dView } from "../../redux/action/postProcessorAction";
import { BASE_URL_FOR_DOWNLOAD, SUB_PATH_FOR_ASSETS } from "../../api/config";
import useRenderTimer from "./useRenderTimer";
import { findGradientSegment, interpolateColor } from "./utils";

const apiMinMax = false;
const ObjDatViewer = ({
  mode,
  setLoader = true,
  loader,
  urlsForFile,
  fileRelatedInfo,
  gradientStops,
  minVal,
  maxVal,
  defaultValues,
  simulationGrounArea,
}) => {
  const dispatch = useDispatch();

  const [repetitionX, setRepetitionX] = useState(1);
  const [repetitionY, setRepetitionY] = useState(1);

  const [objGeometry, setObjGeometry] = useState(null);
  const [datValues, setDatValues] = useState([]);
  const {
    objFileUrl,
    mtlFileUrl,
    datFileUrl,
    objFileKey,
    mtlFileKey,
    datFileKey,
  } = urlsForFile;


  // useEffect(() => {

  //   console.log("url", datFileUrl);
  // }, [urlsForFile])
  // console.log("objFile", objFileUrl, mtlFileUrl, datFileUrl, objFileKey, mtlFileKey, datFileKey);
  const { file: objFile, error: objFileError, isDownloading } = useDownloadAndSaveFile(
    objFileUrl,
    objFileKey,
    {
      ...fileRelatedInfo,
      fileType: "obj",
      fileName: objFileKey,
    },
    setLoader
  );
  const { file: mtlFile, error: mtlFileError } = useDownloadAndSaveFile(
    mtlFileUrl,
    mtlFileKey,
    {
      ...fileRelatedInfo,
      fileType: "mtl",
      fileName: mtlFileKey,

    }
    ,
    setLoader
  );

  const { file: datFile, error: datFileError } = useDownloadAndSaveFile(
    datFileUrl,
    datFileKey,
    {
      ...fileRelatedInfo,
      fileType: "dat",
      fileName: datFileKey,
    }
    ,
    setLoader
  );

  useEffect(() => {
    if (objFileError || mtlFileError || datFileError) {
      console.error("Error downloading OBJ file:", objFileError);
      console.error("Error downloading MTL file:", mtlFileError);
      console.error("Error downloading DAT file:", datFileError);
    }
  }, [objFileError, mtlFileError, datFileError]);

  useRenderTimer("ThreeDViewer");

  useEffect(() => {
    if (!objFile) return;

    const sizeInMB = objFile.size / (1024 * 1024);
    console.log("sizeInMB", sizeInMB);
    let repetition = 1;
    if (sizeInMB > 300) {
      repetition = 1;

      setRepetitionX(repetition);
      setRepetitionY(repetition);
    } else if (sizeInMB > 100) {

      repetition = 2;
      setRepetitionX(repetition);
      setRepetitionY(repetition);
    }
    else {


      const repetitionX =
        simulationGrounArea?.x_repetition > 3
          ? 3
          : simulationGrounArea?.x_repetition;
      const repetitionY =
        simulationGrounArea?.y_repetition > 3
          ? 3
          : simulationGrounArea?.y_repetition;

      setRepetitionX(repetitionX);
      setRepetitionY(repetitionY);
    }

    // console.log(`OBJ File size: ${sizeInMB.toFixed(2)} MB`);
    // console.log(`x_repetition: ${repetitionX}, y_repetition: ${repetitionY}`);

  }, [objFile]);


  // Avoid unnecessary re-execution of expensive logic (checking storage)

  useEffect(() => {
    // if (objFileError || mtlFileError || datFileError) {

    //     console.error("file link error", objFileError, mtlFileError, datFileError)
    checkStorage();
    // }
  }, [objFileError, mtlFileError, datFileError]);


  // const interpolateColor = useCallback(
  //   (start, end, normalizedValue) => {
  //     const startRgb = hexToRgb(start.color);
  //     const endRgb = hexToRgb(end.color);

  //     const rangePercentage =
  //       (normalizedValue - start.percentage) /
  //       (end.percentage - start.percentage);

  //     const r = startRgb.r + rangePercentage * (endRgb.r - startRgb.r);
  //     const g = startRgb.g + rangePercentage * (endRgb.g - startRgb.g);
  //     const b = startRgb.b + rangePercentage * (endRgb.b - startRgb.b);

  //     return { r: Math.round(r), g: Math.round(g), b: Math.round(b) };
  //   },
  //   [hexToRgb]
  // );

  // const findGradientSegment = useCallback(
  //   (normalizedValue) => {
  //     for (let i = 0; i < memoizedGradientStops.length - 1; i++) {
  //       const currentStop = memoizedGradientStops[i];
  //       const nextStop = memoizedGradientStops[i + 1];

  //       if (
  //         normalizedValue >= currentStop.percentage &&
  //         normalizedValue <= nextStop.percentage
  //       ) {
  //         return { start: currentStop, end: nextStop };
  //       }
  //     }
  //     return {
  //       start: memoizedGradientStops[memoizedGradientStops.length - 1],
  //       end: memoizedGradientStops[memoizedGradientStops.length - 1],
  //     };
  //   },
  //   [memoizedGradientStops]
  // );

  const accessFacesWithoutIndex = (object, datValues) => {
    let faceIndex = 0;
    object.traverse((child) => {
      if (child.isMesh) {
        const geometry = child.geometry;
        const positions = geometry.attributes.position.array;
        const colors = [];
        const defaultColor = new THREE.Color(0.5, 0.5, 0.5);
        let min = Infinity;
        let max = -Infinity;

        let localfaceIndex = 0;
        if (!apiMinMax) {
          for (let i = 0; i < positions.length; i += 9) {
            if (localfaceIndex < datValues.length) {
              // Ensure datValues has enough entries
              const datValue = datValues[localfaceIndex];

              if (!isNaN(datValue)) {
                min = Math.min(min, datValue);
                max = Math.max(max, datValue);
              }

              // Always increment localfaceIndex, even if the datValue is NaN
              localfaceIndex++;
            } else {
              // Optional: log a warning if datValues is shorter than expected
              console.warn("datValues is shorter than positions");
              break;
            }
          }
        }

        const minValue = apiMinMax ? defaultValues[0] : min;
        const maxValue = apiMinMax ? defaultValues[1] : max;

        // dispatch(updateMinMaxFor3dView([minValue, maxValue]));

        // console.log("mins", min, max, minVal, maxVal)
        for (let i = 0; i < positions.length; i += 9) {
          const datValue = datValues[faceIndex];

          const color = new THREE.Color();

          if (isNaN(datValue)) {
            // console.log(`NaN found at index ${faceIndex}`);
            color.copy(defaultColor);
          } else {
            let normalizedValue;
            if (minVal && datValue < minVal) {
              normalizedValue = 0;
            } else if (maxVal && datValue > maxVal) {
              normalizedValue = 100;
            } else {
              normalizedValue =
                ((datValue - minValue) / (maxValue - minValue)) * 100;
              // if ((normalizedValue <= 0 || normalizedValue >= 100)) {

              //     console.log("here", normalizedValue, datValue, minValue, maxValue);
              // }
            }

            const { start, end } = findGradientSegment(normalizedValue, gradientStops);

            const interpolatedColor = interpolateColor(
              start,
              end,
              normalizedValue
            );
            color.setRGB(
              interpolatedColor.r / 255,
              interpolatedColor.g / 255,
              interpolatedColor.b / 255
            );
          }

          colors.push(color.r, color.g, color.b);
          colors.push(color.r, color.g, color.b);
          colors.push(color.r, color.g, color.b);

          faceIndex++;
        }

        geometry.setAttribute(
          "color",
          new THREE.Float32BufferAttribute(colors, 3)
        );
        child.material.vertexColors = true;
      }
    });
  }

  const loadOBJWithMaterial = useCallback((objBlob, mtlBlob, onFinish) => {
    const mtlLoader = new MTLLoader();
    const reader = new FileReader();
    setObjGeometry(null);
    reader.onload = (event) => {
      const updatedMtlContent = updateMTLPaths(event.target.result);

      // Parse the updated MTL content
      const materials = mtlLoader.parse(updatedMtlContent);

      materials.preload();

      const objLoader = new OBJLoader();
      objLoader.setMaterials(materials);
      reader.onload = (e) => {
        // console.log("reading")
        const obj = objLoader.parse(e.target.result);
        setObjGeometry(obj);

        setLoader(false);

        if (onFinish) onFinish();
      };
      reader.readAsText(objBlob);
    };
    reader.readAsText(mtlBlob);
  }, []);

  useEffect(() => {

    const timer = setTimeout(() => {
      if (datFile) {

        const start = performance.now();
        setLoader(true)
        const reader = new FileReader();
        reader.onload = (event) => {
          const values = event.target.result.split("\n").map(parseFloat);
          setDatValues(values);
          const end = performance.now();
          console.log(`[1DAT Load] Parsed .dat file in ${(end - start).toFixed(2)} ms`);

        };
        reader.readAsText(datFile);
      }
    }, [100])

    return () => {
      clearTimeout(timer);
    }

  }, [datFile, gradientStops, minVal, maxVal]);

  function updateMTLPaths(mtlContent) {
    // Define the base path you want to replace with 
    const newBasePath = BASE_URL_FOR_DOWNLOAD + SUB_PATH_FOR_ASSETS;


    // Split the MTL file content into lines
    const lines = mtlContent.split("\n");

    // Process each line
    const updatedLines = lines.map((line) => {
      // Check if the line contains map_Kd or map_d (texture map lines)
      if (line.startsWith("map_Kd") || line.startsWith("map_d")) {
        // Extract the filename from the original path
        const parts = line.split(" ");
        const fileName = parts[parts.length - 1].split("/").pop(); // Get the file name
        // Replace with the new path
        // console.log("mtl", `${parts[0]} ${newBasePath}${fileName}`);
        return `${parts[0]} ${newBasePath}${fileName}`;
      }
      // Return other lines unchanged
      return line;
    });

    // Join the updated lines back into a single string
    return updatedLines.join("\n");
  }


  const prevDeps = useRef({ objFile: null, mtlFile: null, mode: null });

  useEffect(() => {

    // const changedDeps = [];
    // if (prevDeps.current.objFile !== objFile) changedDeps.push("objFile");
    // if (prevDeps.current.mtlFile !== mtlFile) changedDeps.push("mtlFile");
    // if (prevDeps.current.mode !== mode) changedDeps.push("mode");

    // console.log("[OBJ Load] useEffect triggered by:", isDownloading, changedDeps);

    // prevDeps.current = { objFile, mtlFile, mode };

    const timer = setTimeout(() => {
      if (objFile && mtlFile && mode === "" && isDownloading === false) {
        const startTime = performance.now();
        setLoader(true);

        loadOBJWithMaterial(objFile, mtlFile, () => {
          const endTime = performance.now();
          // console.log(`[OBJ Load] Total time: ${(endTime - startTime).toFixed(2)} ms`);
        });
      }
    }, 50);

    return () => clearTimeout(timer);
  }, [objFile, mtlFile, mode]);


  const prevDepsDat = useRef({ datValues: null, objFile: null, mode: null, accessFacesWithoutIndex: null, defaultValues: null });
  // console.log("loader", loader);
  useEffect(() => {



    const handler = setTimeout(() => {
      if (datValues.length !== 0 && objFile && mode === "dat") {


        const changedDeps = [];
        if (prevDepsDat.current.objFile !== objFile) changedDeps.push("objFile");
        if (prevDepsDat.current.datValues !== datValues) changedDeps.push("datValues");
        if (prevDepsDat.current.mode !== mode) changedDeps.push("mode");
        if (prevDepsDat.current.accessFacesWithoutIndex !== accessFacesWithoutIndex) changedDeps.push("accessFacesWithoutIndex");
        if (prevDepsDat.current.defaultValues !== defaultValues) changedDeps.push("defaultValues");

        console.log("[DAT Load] useEffect triggered by:", changedDeps);

        prevDepsDat.current = { objFile, datValues, mode, accessFacesWithoutIndex, defaultValues };

        const start = performance.now();
        const loader = new OBJLoader();
        const reader = new FileReader();
        reader.onload = (event) => {
          const obj = loader.parse(event.target.result);
          accessFacesWithoutIndex(obj, datValues);
          setObjGeometry(obj.children[0].geometry);
          setLoader(false);
          const end = performance.now();
          console.log(`[DAT Load] Parsed .dat file in ${(end - start).toFixed(2)} ms`);

        };
        reader.readAsText(objFile);
      }
    }, 300);

    return () => {
      clearTimeout(handler); // Clear the timeout if inputs change before the delay is over
    };
  }, [datValues]);


  const rederGroupOfthreeD = useMemo(() => {

    if (objGeometry && !loader)
      return <group
        position={[
          -((repetitionY - 1) * (simulationGrounArea?.unit_y_length - 0.3)) / 2,
          -((repetitionX - 1) * (simulationGrounArea?.unit_x_length - 0.3)) / 2,
          0,
        ]}
      >
        {mode === "" ?
          [...Array(repetitionX)].map((_, x) =>
            [...Array(repetitionY)].map((_, y) => (
              <primitive
                key={`instance-${x}-${y}`}
                object={objGeometry.clone()}
                position={[
                  y * (simulationGrounArea?.unit_y_length - 0.3),
                  x * (simulationGrounArea?.unit_x_length - 0.3),
                  0,
                ]}
              />
            ))
          )
          :
          [...Array(repetitionX)].map((_, x) =>
            [...Array(repetitionY)].map((_, y) => (
              <ColoredMesh
                key={`instance-colored-${x}-${y}`}
                geometry={objGeometry.clone()}
                position={[
                  y * (simulationGrounArea?.unit_y_length - 0.3),
                  x * (simulationGrounArea?.unit_x_length - 0.3),
                  0,
                ]}
              />
            ))
          )}
      </group>


  }, [objGeometry]);

  if (!(
    (objFile && mtlFile) ||
    (objFile && datFile && mode === "dat")
  ) || loader) {
    // console.log("loader", objFile, mtlFile, loader, mode, datFile);
    return <div style={{ color: 'black' }}>Loading 3D Model...</div>;
  }



  // console.log("hii", objFile, mtlFile, mode, datFile)
  return (
    <Canvas
      style={{ height: "650px" }}
      camera={{ position: [0, 0, 5], fov: 60 }}
    >
      <ambientLight intensity={0.5} />
      <directionalLight position={[10, 10, 10]} intensity={1.5} />

      {/* Calculate the offsets to center the group */}
      {objGeometry && !loader &&
        rederGroupOfthreeD
      }

      <OrbitControls

        enableZoom={true} zoomSpeed={0.6} minDistance={0} maxDistance={50}
      />
    </Canvas>

  );
};

const ColoredMesh = memo(({ geometry, position, rotation }) => (
  <mesh geometry={geometry} position={position} rotation={rotation}>
    <meshStandardMaterial vertexColors={true} />
  </mesh>
));

export default memo(ObjDatViewer);
