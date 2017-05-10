package org.jenkinsci.plugins.scmanywhere;

import java.io.Serializable;

import hudson.scm.SCMRevisionState;

public class SCMAnyWhereRevisionState extends SCMRevisionState implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String revNo;
	private final String revDataTime;

	public SCMAnyWhereRevisionState(String revNo , String revDataTime) {
		this.revNo = revNo;
		this.revDataTime = revDataTime;
	}

	public String getRevNo() {
		return this.revNo;
	}

	public String getRevDateTime() {
		return this.revDataTime;
	}

	@Override
	public String toString() {
		return "RevisionState : " + this.revNo  +  " "+ revDataTime;
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof SCMRevisionState) {
			SCMAnyWhereRevisionState state = (SCMAnyWhereRevisionState) other;
			result = this.revNo.equals(state.revNo) && this.revDataTime.equals(state.revDataTime);		 
		}
		return result;
	}

	@Override
	public int hashCode() {
		return this.revNo.hashCode() + this.revDataTime.hashCode();
	}
}
