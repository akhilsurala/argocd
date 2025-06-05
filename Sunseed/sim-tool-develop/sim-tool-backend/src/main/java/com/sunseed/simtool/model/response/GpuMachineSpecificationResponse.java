package com.sunseed.simtool.model.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GpuMachineSpecificationResponse {

	private int code;
	private List<GpuMachineData> data;

	@Getter
	@Setter
	public static class GpuMachineData {
		private String name;
		private String plan;
		private String image;
		private OperatingSystem os;
		private String location;
		private GpuMachineSpecs specs;
		private String cpuType;
		private GpuCardDetails gpuCardDetails;
		private String nodeDescription;
		private Map<String, String> installedApplicationVersion;
		private CanSupportBitninja canSupportBitninja;
		private int bitninjaDiscountPercentage;
		private boolean availableInventoryStatus;
		private String currency;
		private boolean isBlockstorageAttachable;
	}

	@Getter
	@Setter
	public static class OperatingSystem {
		private String name;
		private String version;
		private String image;
		private String category;
	}

	@Getter
	@Setter
	public static class GpuMachineSpecs {
		private String id;
		private String skuName;
		private String ram;
		private int cpu;
		private int diskSpace;
		private int pricePerMonth;
		private int pricePerHour;
		private String series;
		private int minimumBillingAmount;
		private List<CommittedSku> committedSku;
		private String family;
		private String cudaVersion;
	}

	@Getter
	@Setter
	public static class CommittedSku {
		private int committedSkuId;
		private String committedSkuName;
		private String committedNodeMessage;
		private int committedSkuPrice;
		private String committedUptoDate;
		private int committedDays;
	}

	@Getter
	@Setter
	public static class GpuCardDetails {

		@JsonProperty("CARD_NAME")
		private String cardName;

		@JsonProperty("TEMPLATE_ID")
		private String templateId;

		@JsonProperty("MEMORY_UNIT")
		private String memoryUnit;

		@JsonProperty("MEMORY")
		private String memory;

		@JsonProperty("CARD_TYPE")
		private String cardType;
	}

	@Getter
	@Setter
	public static class CanSupportBitninja {
		private boolean showBitninja;
		private int bitninjaCost;
	}
}
