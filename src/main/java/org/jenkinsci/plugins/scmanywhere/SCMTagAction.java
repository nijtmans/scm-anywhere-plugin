package org.jenkinsci.plugins.scmanywhere;

import hudson.model.Run;
import hudson.scm.AbstractScmTagAction;

import org.kohsuke.stapler.export.ExportedBean;

/**
 * A Tag Action allows a user to tag a build. Repo doesn't support a solid tag
 * method, so right now we just display the static manifest information needed
 * to recreate the exact state of the repository when the build was ran.
 */
@ExportedBean(defaultVisibility = 999)
public class SCMTagAction extends AbstractScmTagAction {

	/**
	 * Constructs the tag action object. Just call the superclass.
	 *
	 * @param build
	 *            Build which we are interested in tagging
	 */
	SCMTagAction(final Run<?, ?> build) {
		super(build);
	}

	/**
	 * Returns the filename to use as the badge. Called by the default badge
	 * jelly file.
	 */
	public String getIconFileName() {
		return "star.gif";
	}

	/**
	 * Returns the display name to use for the tag action. Called by the default
	 * badge jelly file.
	 */
	public String getDisplayName() {
		return "Dynam Soft SCM any Where";
	}

	@Override
	public boolean isTagged() {
		return false;
	}

}
