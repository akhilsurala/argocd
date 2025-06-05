export default function validationCrop(data,objCycleName,objIndex,cropTypeValue, optionalCropTypeValue) {
    const cropMap = {}; // Object to store composite key and value
    const cropSet = new Set(); // Set to store unique crop values
  
    // Helper function to create composite key for cropId1
    function createCropId1Key(cycleName, bedIndex) {
      return `${cycleName}_${bedIndex}_cropId1`;
    }
    function createOptionalCropKey(cycleName, bedIndex) {
        return `${cycleName}_${bedIndex}_optionalCropType`;
      }
  
    function storeData(key, value) {
        if (cropMap[key]) {
            cropSet.delete(cropMap[key]);
          }
  
          cropMap[key] = value;
          cropSet.add(value);
    }
    // Helper function to create composite key for optionalCropType
   
  
    data.forEach((cycle) => {
      cycle.cycleBedDetails.forEach((bedDetail, bedIndex) => {
        const { cropId1, optionalCropType } = bedDetail;
  
        if (cropId1) {
          const cropIdKey = createCropId1Key(cycle.cycleName, bedIndex);
          storeData(cropIdKey,cropId1)
        }
  
        if (optionalCropType) {
          const optionalCropKey = createOptionalCropKey(cycle.cycleName, bedIndex);
  
          storeData(optionalCropKey,optionalCropType)
        }
      });

      if (cropTypeValue) {
        const cropIdKey = createCropId1Key(objCycleName, objIndex);
        storeData(cropIdKey,cropTypeValue)
      }

      if (optionalCropTypeValue) {
        const optionalCropKey = createOptionalCropKey(objCycleName, objIndex);

        storeData(optionalCropKey,optionalCropTypeValue)
      }

      
    });
  
    // console.log("Object (cropMap):", cropMap);
    // console.log("Set (cropSet):", cropSet);
    return cropSet.size;
  }
  