package com.sunseed.simtool.validation;

import org.springframework.stereotype.Component;

@Component
public class ValidationFormulas {

	/**
	 * @param lengthOfOneRow L, length of one row north to south
	 * @param lengthOfField LL, length of field from east to west 
	 * @param noOfModules n, No. of modules in a panel
	 * @param lengthOfModule l(in case of P module config), length of a panel
	 * @param widthOfModule l(in case of L module config), width of a panel
	 * @param tiltAngle angle, tilt angle
	 * @param isEwPanel Pn-Pn or Ln-Ln
	 * @param panelOrientation P or L
	 * @return
	 */
	public Double calculateMaxGapBetweenModules(Double lengthOfOneRow, Double lengthOfField, Integer noOfModules, Double lengthOfModule,
			Double widthOfModule, Double tiltAngle, Boolean isEwPanel, char panelOrientation)
	{
		//P1, L1
		QuintFunction<Double, Double, Integer, Double, Double, Double> gapBetweenModulesFunctionSingleModule =  
				(L, LL, n, l, angle) -> L-l;
		
		//P2, P3, P4, L2, L3, L4
		QuintFunction<Double, Double, Integer, Double, Double, Double> gapBetweenModulesFunction = 
				(L, LL, n, l, angle) -> (LL - (Math.cos(Math.toRadians(angle)) * n * l) )/(Math.cos(Math.toRadians(angle)) * (n-1));
				
		//Pn-Pn, Ln-Ln
		QuintFunction<Double, Double, Integer, Double, Double, Double> gapBetweenModulesFunctionEWPanles = 
				(L, LL, n, l, angle) -> (LL - (Math.cos(Math.toRadians(angle)) * 2*n * l) )/(Math.cos(Math.toRadians(angle)) * (2*n-1));
		
		Double gap = 0.0;
		
		
		////////////////////////////////
		if (noOfModules == 1 || isEwPanel == true) {
			if (noOfModules == 1) {
				if (panelOrientation == 'P')
					gap = gapBetweenModulesFunctionSingleModule.apply(lengthOfOneRow, lengthOfField, noOfModules,
							widthOfModule, tiltAngle);
				else if (panelOrientation == 'L')
					gap = gapBetweenModulesFunctionSingleModule.apply(lengthOfOneRow, lengthOfField, noOfModules,
							lengthOfModule, tiltAngle);
			}

			if (isEwPanel) {
				if (panelOrientation == 'P')
					gap = gapBetweenModulesFunctionEWPanles.apply(lengthOfOneRow, lengthOfField, noOfModules,
							lengthOfModule, tiltAngle);
				else if (panelOrientation == 'L')
					gap = gapBetweenModulesFunctionEWPanles.apply(lengthOfOneRow, lengthOfField, noOfModules,
							widthOfModule, tiltAngle);
			}
		}
		
		else
		{
			if(panelOrientation == 'P')
				gap = gapBetweenModulesFunction.apply(lengthOfOneRow, lengthOfField, noOfModules, lengthOfModule, tiltAngle);
			else if(panelOrientation == 'L')
				gap = gapBetweenModulesFunction.apply(lengthOfOneRow, lengthOfField, noOfModules, widthOfModule, tiltAngle);
		}
		
		
		
		////////////////////////////////

		
//		if(isEwPanel)
//		{
//			if(panelOrientation == 'P')
//				gap = gapBetweenModulesFunctionEWPanles.apply(lengthOfOneRow, lengthOfField, noOfModules, lengthOfModule, tiltAngle);
//			else if(panelOrientation == 'L')
//				gap = gapBetweenModulesFunctionEWPanles.apply(lengthOfOneRow, lengthOfField, noOfModules, widthOfModule, tiltAngle);
//		}
//		else
//		{
//			if(noOfModules == 1)
//			{
//				if(panelOrientation == 'P')
//					gap = gapBetweenModulesFunctionSingleModule.apply(lengthOfOneRow, lengthOfField, noOfModules, lengthOfModule, tiltAngle);
//				else if(panelOrientation == 'L')
//					gap = gapBetweenModulesFunctionSingleModule.apply(lengthOfOneRow, lengthOfField, noOfModules, widthOfModule, tiltAngle);
//			}
//			else
//			{
//				if(panelOrientation == 'P')
//					gap = gapBetweenModulesFunction.apply(lengthOfOneRow, lengthOfField, noOfModules, lengthOfModule, tiltAngle);
//				else if(panelOrientation == 'L')
//					gap = gapBetweenModulesFunction.apply(lengthOfOneRow, lengthOfField, noOfModules, widthOfModule, tiltAngle);
//			}
//		}
		
		return gap;
	}
	
	
	/**
	 * @param noOfModules n, number of modules in a panel
	 * @param lengthOfModule 
	 * @param widthOfModule
	 * @param tiltAngle angle, tilt angle
	 * @param panelOrientation P or L
	 * @param gapBetweenModules
	 * @return
	 */
	public Double calculateModuleHeight(Integer noOfModules, Double lengthOfModule, Double widthOfModule, Double tiltAngle,
			char panelOrientation, Double gapBetweenModules)
	{	
		QuadFunction<Double, Integer, Double, Double, Double> heightFunction =  
				(l, n, angle, gap) -> (Math.sin(Math.toRadians(angle)) * ( (l*n) + (n-1)*gap ))/2.0;	
		//l -> length for 'P', l -> width for 'L'
				
		if(panelOrientation == 'L')
			return heightFunction.apply(widthOfModule, noOfModules, tiltAngle, gapBetweenModules);
		else if(panelOrientation == 'P')
			return heightFunction.apply(lengthOfModule, noOfModules, tiltAngle, gapBetweenModules);
		
		return 0.0;
	}
	
	/**
	 * @param noOfModules
	 * @param lengthOfModule
	 * @param widthOfModule
	 * @param tiltAngle
	 * @param panelOrientation P or L
	 * @param gapBetweenModules
	 * @param isEwPanel Pn-Pn or Ln-Ln
	 * @return
	 */
	public Double calculatePitch(Integer noOfModules, Double lengthOfModule, Double widthOfModule, Double tiltAngle,
			char panelOrientation, Double gapBetweenModules, Boolean isEwPanel)
	{	
		QuadFunction<Double, Integer, Double, Double, Double> pitchFunction =  
				(l, n, angle, gap) -> (( (l*n) + (n-1)*gap )); // for projected length = (Math.cos(Math.toRadians(angle)) * ( (l*n) + (n-1)*gap ));
		QuadFunction<Double, Integer, Double, Double, Double> pitchFunctionEwPanel =  
				(l, n, angle, gap) -> (( (l*n*2) + (2*n-1)*gap ));
		//l -> length for 'P', l -> width for 'L'		
		
		if(isEwPanel)
		{
			if(panelOrientation == 'L')
				return pitchFunctionEwPanel.apply(widthOfModule, noOfModules, tiltAngle, gapBetweenModules);
			else if(panelOrientation == 'P')
				return pitchFunctionEwPanel.apply(lengthOfModule, noOfModules, tiltAngle, gapBetweenModules);
		}
		else
		{
			if(panelOrientation == 'L')
				return pitchFunction.apply(widthOfModule, noOfModules, tiltAngle, gapBetweenModules);
			else if(panelOrientation == 'P')
				return pitchFunction.apply(lengthOfModule, noOfModules, tiltAngle, gapBetweenModules);
		}
		
		return 0.0;
	}
	
	public Double calculateBedBottomWidth(Double bedAngle, Double bedTopWidth, Double bedHeight)
	{
		Double bedBottomWidth = bedTopWidth + 2*(bedHeight/Math.tan(Math.toRadians(bedAngle)));
		return bedBottomWidth;
	}
	
	public Double calculateBedSpacing(Integer bedCount, Double pitch, Double bedBottomWidth)
	{
		Double bedSpacing = bedCount > 0 ? (pitch - (bedCount * bedBottomWidth))/bedCount : 0;
		return bedSpacing;
	}
}
