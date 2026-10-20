package com.chaitanya.utils;

import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.beans.BeanUtils;


public class AppUtils {

	private AppUtils() {
	}

	/**
	 * Random 3 digit Id generator
	 * */
	public static String randomID() {
		return UUID.randomUUID().toString().substring(0, 5);
	}

	/**
	 * Used to copy one object to another
	 * This method can be used to set required data to DTO from persistence Objects
	 * */
	public static <K, T> K convertToDTO(T src, K trgt) {
		BeanUtils.copyProperties(src, trgt);
		return trgt;
	}

	/**
	 * Added to get current timestamp
	 * Can be used globally, if needed implementation can be changed at a single change
	 * */
	public static Timestamp currentTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

}
