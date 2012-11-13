package edu.worcester.cs499summer2012.activity;

import com.google.common.base.Preconditions;


/**
 * API key found in the <a href="https://code.google.com/apis/console/?api=tasks">Google apis
 * console</a>.
 * 
 * <p>
 * Once at the Google apis console, click on "Add project...". If you've already set up a project,
 * you may use that one instead, or create a new one by clicking on the arrow next to the project
 * name and click on "Create..." under "Other projects". Finally, click on "API Access". Look for
 * the section at the bottom called "Simple API Access".
 * </p>
 * 
 * @author Yaniv Inbar
 */
public class ClientCredentials {

	/** Value of the "API key" shown under "Simple API Access". */
	public static final String KEY = null;

	public static void errorIfNotSpecified() {
		Preconditions.checkNotNull(KEY,
				"Please enter your API key from https://code.google.com/apis/console/?api=tasks in "
						+ ClientCredentials.class);
	}
}
