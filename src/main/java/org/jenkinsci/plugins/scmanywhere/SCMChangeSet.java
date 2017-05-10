
package org.jenkinsci.plugins.scmanywhere;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;

import java.util.Iterator;
import java.util.List;

/**
 * A ChangeLogSet, which is used when generating the list of changes from one
 * build to the next.
 */
public class SCMChangeSet extends ChangeLogSet<SCMChangeLogEntry> {
	private final List<SCMChangeLogEntry> logs;

	/**
	 * Object Constructor. Call the super class, initialize our variable, and
	 * set us as the parent for all of our children.
	 *
	 * @param build
	 *            The build which caused this change log.
	 * @param logs
	 *            a list of SCM any where ChangeLogEntry, containing every change (commit)
	 *            which has occurred since the last build.
	 */
	protected SCMChangeSet(final AbstractBuild<?, ?> build, final List<SCMChangeLogEntry> logs) {
		super(build);
		this.logs = logs;
		for (final SCMChangeLogEntry log : logs) {
			log.setParent(this);
		}
	}

	/**
	 * Returns an iterator for our SCM any where ChangeLogEntry list. This is used when
	 * generating the Web UI.
	 */
	public Iterator<SCMChangeLogEntry> iterator() {
		return logs.iterator();
	}

	@Override
	public boolean isEmptySet() {
		return logs.isEmpty();
	}

	@Override
	public String getKind() {
		return "DynamSoft AnyWhere";
	}
}
