package org.jruby.runtime.profile;

public interface IProfileData {

    /**
     * Begin profiling a new method, aggregating the current time diff in the previous
     * method's profile slot.
     *
     * @param nextMethod the serial number of the next method to profile
     * @return the serial number of the previous method being profiled
     */
	public int profileEnter(int nextMethod);

    /**
     * Fall back to previously profiled method after current method has returned.
     *
     * @param nextMethod the serial number of the next method to profile
     * @param startTime the nanotime when this invocation began
     * @return the serial number of the previous method being profiled
     */
	public int profileExit(int nextMethod, long startTime);
}