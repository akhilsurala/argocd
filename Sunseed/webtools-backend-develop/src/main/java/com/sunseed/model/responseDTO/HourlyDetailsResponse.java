package com.sunseed.model.responseDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HourlyDetailsResponse {
private List<Double> b1C1Sunlit;
private List<Double> b1C1Shaded;
private List<Double> b1AllSunlit;
private List<Double> b2AllSunlit;
private List<Double> allAllAll;
//    private Combination combination;
//    private
}



//{
//        "data": [{
//combination: {"bed_indexes" : "1", "crop_name" : "sorghum", "leafType" : "ALL"},
//response: [
//        23.12,
//        57.5,
//        34.88,
//        3.79,
//        65.36,
//        53.48,
//        84.73,
//        81.2,
//        7.98,
//        81.19,
//        55.04,
//        49.49,
//        49.33,
//        12.05,
//        14.32,
//        83.49,
//        33.85,
//        93.39,
//        36.82,
//        52.18,
//        60.56,
//        51.53,
//        68.22,
//        40.09
//        ],
//        },{
//combination: {"bed_indexes" : "1", "crop_name" : "sorghum",  "leafType" : "ALL"},
//response: [
//        23.12,
//        57.5,
//        34.88,
//        3.79,
//        65.36,
//        53.48,
//        84.73,
//        81.2,
//        7.98,
//        81.19,
//        55.04,
//        49.49,
//        49.33,
//        12.05,
//        14.32,
//        83.49,
//        33.85,
//        93.39,
//        36.82,
//        52.18,
//        60.56,
//        51.53,
//        68.22,
//        40.09
//        ]
//        },{
//combination: {"bed_indexes" : "2", "crop_name" : "sorghum", "leafType" : "sunlit"},
//response: [
//        23.12,
//        57.5,
//        34.88,
//        3.79,
//        65.36,
//        53.48,
//        84.73,
//        81.2,
//        7.98,
//        81.19,
//        55.04,
//        49.49,
//        49.33,
//        12.05,
//        14.32,
//        83.49,
//        33.85,
//        93.39,
//        36.82,
//        52.18,
//        60.56,
//        51.53,
//        68.22,
//        40.09
//        ]
//        },{
//combination: {"bed_indexes" : "3", "crop_name" : "sorghum",  "leafType" : "sunshade"},
//response: [
//        5.2,
//        34.18,
//        64.27,
//        50.66,
//        23.16,
//        60.08,
//        0.14,
//        44.16,
//        12.19,
//        35.55,
//        21.25,
//        21.14,
//        82.14,
//        13.44,
//        23.01,
//        72.83,
//        48.65,
//        43.78,
//        84.35,
//        15.26,
//        61.28,
//        1.92,
//        68.99,
//        44.52
//        ],
//        },{
//combination: {"bed_indexes" : "ALL", "crop_name" : "sorghum", "leafType" : "ALL"},
//response: [
//        18.13,
//        71.44,
//        88.17,
//        80.38,
//        18.14,
//        87.01,
//        85.7,
//        36.38,
//        21.33,
//        42.56,
//        92.39,
//        77.77,
//        72.4,
//        83.95,
//        66.32,
//        88.29,
//        37.78,
//        41.95,
//        37.72,
//        16.07,
//        90.83,
//        61.58,
//        23.21,
//        7.63
//        ]
//        }],
//        "success": true,
//        "httpStatus": "OK",
//        "message": "Post processing details fetched successfully"
//        }