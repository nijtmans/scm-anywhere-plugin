package org.jenkinsci.plugins.scmanywhere;

import hudson.scm.SCMRevisionState;

public class SCMRevisionSate extends SCMRevisionState {

	private final String revNo;
	private final String rev_id;

	public SCMRevisionSate(String revNo, String revId) {
		this.revNo = revNo;
		this.rev_id = revId;
	}

	public String getRevNo() {
		return this.revNo;
	}

	public String getRevId() {
		return this.rev_id;
	}

	@Override
	public String toString() {
		return "RevisionState revno:" + this.revNo + " revid:" + this.rev_id;
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof SCMRevisionSate) {
			SCMRevisionSate that = (SCMRevisionSate) other;
			result = this.rev_id.equals(that.rev_id);
		}
		return result;
	}

	@Override
	public int hashCode() {
		return this.rev_id.hashCode();
	}
}
