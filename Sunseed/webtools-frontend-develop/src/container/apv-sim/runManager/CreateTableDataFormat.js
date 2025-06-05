import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";
import { changeDateFormat, getApvToggle } from "../../../utils/constant";
dayjs.extend(utc);
dayjs.extend(timezone);

export const CreateTableDataFormat = (data, hasChildRuns = true) => {
  const createData = (data) => {
    const pvParameters = data.pvParameters;
    const agriGeneralParameters = data.agriGeneralParameters;
    const cropParameters = data.cropParameters;
    const economicParameters = data.economicParameters;
  
    const cycles = cropParameters?.cycles;
    let cropParametersDataObj = {};
    cycles?.map((data, index) => {
      cropParametersDataObj[`cycleStartDate${index}`] = data.cycleStartDate;
      cropParametersDataObj[`cycleBedDetails${index}`] = data.cycleBedDetails;
      cropParametersDataObj[`interBedPattern${index}`] = data.interBedPattern;
      cropParametersDataObj[`cycleName${index}`] = data.cycleName;
    });

    const result = {
      projectName: "Project A",
      makeControl: "Simulating",
      runName: data.runName,
      status: data.runStatus,
      agriControl: data.agriControl,
      pvControl: data.pvControl,
      projectId: data?.projectId,
      runId: data?.id,
      simulationType: getApvToggle(data?.preProcessorToggle.toggle),
      isMaster: data?.isMaster,
      variantExist: data?.variantExist,
      runStatus: data?.runStatus,

      // PV parameters fields
      tiltIfFt: pvParameters?.tiltIfFt ? pvParameters?.tiltIfFt+" deg": '',
      maxAngleOfTracking: pvParameters?.maxAngleOfTracking ? pvParameters?.maxAngleOfTracking+" deg" : "",
      moduleMaskPattern: pvParameters?.moduleMaskPattern,
      gapBetweenModules: pvParameters?.gapBetweenModules ? pvParameters?.gapBetweenModules+ " mm":'',
      height: pvParameters?.height ? pvParameters?.height+" m": '',
      Xcoordinate: pvParameters?.Xcoordinate,
      Ycoordinate: pvParameters?.Ycoordinate,
      pvModule: pvParameters?.pvModule?.moduleType,
      modeOfOperationId: pvParameters?.modeOfOperationId?.modeOfOperation,
      moduleConfigs: pvParameters?.moduleConfigs[0].moduleConfig,
      createdOn: changeDateFormat(dayjs.utc(data.createdAt).tz(dayjs.tz.guess())),
      // createdOn: data.createdAt,

      pitchOfRows: data?.preProcessorToggle?.pitchOfRows ? data?.preProcessorToggle?.pitchOfRows+" m" : "",
      azimuth: data?.preProcessorToggle?.azimuth ? data?.preProcessorToggle?.azimuth+ " deg": "",
      lengthOfOneRow: data?.preProcessorToggle?.lengthOfOneRow ? data?.preProcessorToggle?.lengthOfOneRow+" m": "",
      soilType: data?.preProcessorToggle?.soilType?.soilName ? data?.preProcessorToggle?.soilType?.soilName : "",

      //Agri General Parameters fields
      irrigationType: agriGeneralParameters?.irrigationType?.irrigationType,
      tempControl: agriGeneralParameters?.tempControl ? agriGeneralParameters?.tempControl: "",
      minTemp: agriGeneralParameters?.minTemp? agriGeneralParameters?.minTemp+" deg" : '',
      maxTemp: agriGeneralParameters?.maxTemp ? agriGeneralParameters?.maxTemp+" deg" : "",

      trail: agriGeneralParameters?.trail ? agriGeneralParameters?.trail + " deg": "",
      agriPvProtectionHeight: agriGeneralParameters?.agriPvProtectionHeight ? agriGeneralParameters?.agriPvProtectionHeight: "",
      isMulching: agriGeneralParameters?.isMulching ? agriGeneralParameters?.isMulching ? "Yes" : "No" : "",
      bedWidth: agriGeneralParameters?.bedParameter?.bedWidth ? agriGeneralParameters?.bedParameter?.bedWidth + " mm": "",
      bedHeight: agriGeneralParameters?.bedParameter?.bedHeight ? agriGeneralParameters?.bedParameter?.bedHeight + " mm": "",
      bedAngle: agriGeneralParameters?.bedParameter?.bedAngle ? agriGeneralParameters?.bedParameter?.bedAngle +" deg" : "",
      bedAzimuth:agriGeneralParameters ? agriGeneralParameters?.bedParameter?.bedAzimuth === 0?"ALONG":"ACROSS" : "",
      bedcc:agriGeneralParameters?.bedParameter?.bedcc? agriGeneralParameters?.bedParameter?.bedcc: "",
      startPointOffset: agriGeneralParameters?.bedParameter?.startPointOffset ? agriGeneralParameters?.bedParameter?.startPointOffset+" mm" : "",

      // Crop Parameters
      ...cropParametersDataObj,
      cycleBedDetails: cycles,

      //Economic Parameters
      currency: economicParameters?.currency?.currency,
      // minReferenceYieldCost: economicParameters?.minReferenceYieldCost,
      // maxReferenceYieldCost: economicParameters?.maxReferenceYieldCost,
      // minInputCostOfCrop: economicParameters?.minInputCostOfCrop,
      // maxInputCostOfCrop: economicParameters?.maxInputCostOfCrop,
      // minSellingCostOfCrop: economicParameters?.minSellingCostOfCrop,
      // maxSellingCostOfCrop: economicParameters?.maxSellingCostOfCrop,
      hourlySellingRates: getApvToggle(data?.preProcessorToggle.toggle) !== 'Only Agri' ? economicParameters?.hourlySellingRates: [],
      economicParameter : economicParameters?.economicMultiCropResponseList ? economicParameters?.economicMultiCropResponseList : "",
      numberOfRuns: 0,
      beds: "View Beds",
      comments: "hey this is comment",
      location:null ,
    };

    result.hasChildRuns = hasChildRuns;

    return result;
  };

  let dataList = [];

  data.runs.map((data) => {
    const obj = createData(data);
    dataList.push(obj);
  });

  return dataList;
};
