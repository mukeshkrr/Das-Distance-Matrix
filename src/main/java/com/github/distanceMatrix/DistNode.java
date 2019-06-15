package com.github.distanceMatrix;

import lombok.Data;

@Data
public class DistNode {
	
	private Long id;
	private String bookingId;
	private int weightIndex;
	private String dropLatitude;	
	private String dropLongitude;
}
