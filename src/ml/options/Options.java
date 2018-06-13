package ml.options;

import java.util.Iterator;

/**
 * The central class for option processing. Sets are identified by their name,
 * but there is also an anonymous default set, which is very convenient if an
 * application requieres only one set.
 */

public class Options {

	private final static String CLASS = "Options";

	/**
	 * The name used internally for the default set
	 */

	public final static String DEFAULT_SET = "DEFAULT_OPTION_SET";

	/**
	 * An enum encapsulating the possible separators between value options and
	 * their actual values.
	 */

	public static final class Separator {

		/**
		 * Separate option and value by ":"
		 */

		public static final Separator COLON = new Separator(':');

		/**
		 * Separate option and value by "="
		 */
		public static final Separator EQUALS = new Separator('=');

		/**
		 * Separate option and value by blank space
		 */
		public static final Separator BLANK = new Separator(' '); // Or, more
		// precisely,
		// whitespace
		// (as
		// allowed
		// by the
		// CLI)

		/**
		 * This is just a placeholder in case no separator is required (i. e.
		 * for non-value options)
		 */
		public static final Separator NONE = new Separator('D'); // NONE is a
		// placeholder
		// in case
		// no
		// separator
		// is
		// required,
		// 'D' is
		// just an
		// arbitrary
		// dummy
		// value

		private char c;

		private Separator(char c) {
			this.c = c;
		}

		/**
		 * Return the actual separator character
		 * <p>
		 * 
		 * @return The actual separator character
		 */

		char getName() {
			return c;
		}

	}

	/**
	 * An enum encapsulating the possible prefixes identifying options (and
	 * separating them from command line data items)
	 */

	public static final class Prefix {

		/**
		 * Options start with a "-" (typically on Unix platforms)
		 */
		public static final Prefix DASH = new Prefix('-');

		/**
		 * Options start with a "/" (typically on Windows platforms)
		 */
		public static final Prefix SLASH = new Prefix('/');

		private char c;

		private Prefix(char c) {
			this.c = c;
		}

		/**
		 * Return the actual prefix character
		 * <p>
		 * 
		 * @return The actual prefix character
		 */

		char getName() {
			return c;
		}

	}

	/**
	 * An enum encapsulating the possible multiplicities for options
	 */

	public static final class Multiplicity {

		/**
		 * Option needs to occur exactly once
		 */
		public static final Multiplicity ONCE = new Multiplicity(0);

		/**
		 * Option needs to occur at least once
		 */
		public static final Multiplicity ONCE_OR_MORE = new Multiplicity(1);

		/**
		 * Option needs to occur either once or not at all
		 */

		public static final Multiplicity ZERO_OR_ONE = new Multiplicity(2);

		/**
		 * Option can occur any number of times
		 */

		public static final Multiplicity ZERO_OR_MORE = new Multiplicity(3);

		public int getValue() {
			return i;
		}

		private int i;

		private Multiplicity(int i) {
			this.i = i;
		}
	}

	private java.util.HashMap optionSets = new java.util.HashMap();
	private Prefix prefix = null;
	private Multiplicity defaultMultiplicity = null;
	private String[] arguments = null;
	private boolean ignoreUnmatched = false;
	private int defaultMinData = 0;
	private int defaultMaxData = 0;
	private StringBuffer checkErrors = null;

	/**
	 * Constructor
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param prefix
	 *            The prefix to use for all command line options. It can only be
	 *            set here for all options at the same time
	 * @param defaultMultiplicity
	 *            The default multiplicity to use for all options (can be
	 *            overridden when adding an option)
	 * @param defMinData
	 *            The default minimum number of data items for all sets (can be
	 *            overridden when adding a set)
	 * @param defMaxData
	 *            The default maximum number of data items for all sets (can be
	 *            overridden when adding a set)
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If either <code>args</code>, <code>prefix</code>, or
	 *             <code>defaultMultiplicity</code> is <code>null</code> - or if
	 *             the data range values don't make sense
	 * 
	 *             From JavaWorld article by Dr. Matthias Laux *
	 *             (http://www.javaworld
	 *             .com/javaworld/jw-08-2004/jw-0816-command.html) with changes
	 *             made to accomodate Java 1.4
	 */

	public Options(String args[], Prefix prefix, Multiplicity defaultMultiplicity, int defMinData, int defMaxData) {

		if (args == null) {
			throw new IllegalArgumentException(CLASS + ": args may not be null");
		}
		if (prefix == null) {
			throw new IllegalArgumentException(CLASS + ": prefix may not be null");
		}
		if (defaultMultiplicity == null) {
			throw new IllegalArgumentException(CLASS + ": defaultMultiplicity may not be null");
		}

		if (defMinData < 0) {
			throw new IllegalArgumentException(CLASS + ": defMinData must be >= 0");
		}
		if (defMaxData < defMinData) {
			throw new IllegalArgumentException(CLASS + ": defMaxData must be >= defMinData");
		}

		arguments = new String[args.length];
		for (int i = 0; i < args.length; ++i) {
			arguments[i] = args[i];
		}

		this.prefix = prefix;
		this.defaultMultiplicity = defaultMultiplicity;
		this.defaultMinData = defMinData;
		this.defaultMaxData = defMaxData;
	}

	/**
	 * Constructor
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param prefix
	 *            The prefix to use for all command line options. It can only be
	 *            set here for all options at the same time
	 * @param defaultMultiplicity
	 *            The default multiplicity to use for all options (can be
	 *            overridden when adding an option)
	 * @param data
	 *            The default minimum and maximum number of data items for all
	 *            sets (can be overridden when adding a set)
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If either <code>args</code>, <code>prefix</code>, or
	 *             <code>defaultMultiplicity</code> is <code>null</code> - or if
	 *             the data range value doesn't make sense
	 */

	public Options(String args[], Prefix prefix, Multiplicity defaultMultiplicity, int data) {
		this(args, prefix, defaultMultiplicity, data, data);
	}

	/**
	 * Constructor. The default number of data items is set to 0.
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param prefix
	 *            The prefix to use for all command line options. It can only be
	 *            set here for all options at the same time
	 * @param defaultMultiplicity
	 *            The default multiplicity to use for all options (can be
	 *            overridden when adding an option)
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If either <code>args</code>, <code>prefix</code>, or
	 *             <code>defaultMultiplicity</code> is <code>null</code>
	 */

	public Options(String args[], Prefix prefix, Multiplicity defaultMultiplicity) {
		this(args, prefix, defaultMultiplicity, 0, 0);
	}

	/**
	 * Constructor. The prefix is set to {@link Prefix#DASH}.
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param defaultMultiplicity
	 *            The default multiplicity to use for all options (can be
	 *            overridden when adding an option)
	 * @param defMinData
	 *            The default minimum number of data items for all sets (can be
	 *            overridden when adding a set)
	 * @param defMaxData
	 *            The default maximum number of data items for all sets (can be
	 *            overridden when adding a set)
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If either <code>args</code> or
	 *             <code>defaultMultiplicity</code> is <code>null</code> - or if
	 *             the data range values don't make sense
	 */

	public Options(String args[], Multiplicity defaultMultiplicity, int defMinData, int defMaxData) {
		this(args, Prefix.DASH, defaultMultiplicity, defMinData, defMaxData);
	}

	/**
	 * Constructor. The prefix is set to {@link Prefix#DASH}.
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param defaultMultiplicity
	 *            The default multiplicity to use for all options (can be
	 *            overridden when adding an option)
	 * @param data
	 *            The default minimum and maximum number of data items for all
	 *            sets (can be overridden when adding a set)
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If either <code>args</code> or
	 *             <code>defaultMultiplicity</code> is <code>null</code> - or if
	 *             the data range value doesn't make sense
	 */

	public Options(String args[], Multiplicity defaultMultiplicity, int data) {
		this(args, Prefix.DASH, defaultMultiplicity, data, data);
	}

	/**
	 * Constructor. The prefix is set to {@link Prefix#DASH}, and the default
	 * number of data items is set to 0.
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param defaultMultiplicity
	 *            The default multiplicity to use for all options (can be
	 *            overridden when adding an option)
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If either <code>args</code> or
	 *             <code>defaultMultiplicity</code> is <code>null</code>
	 */

	public Options(String args[], Multiplicity defaultMultiplicity) {
		this(args, Prefix.DASH, defaultMultiplicity, 0, 0);
	}

	/**
	 * Constructor. The prefix is set to {@link Prefix#DASH}, the default number
	 * of data items is set to 0, and the multiplicity is set to
	 * {@link Multiplicity#ONCE}.
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If <code>args</code> is <code>null</code>
	 */

	public Options(String args[]) {
		this(args, Prefix.DASH, Multiplicity.ONCE);
	}

	/**
	 * Constructor. The prefix is set to {@link Prefix#DASH}, and the
	 * multiplicity is set to {@link Multiplicity#ONCE}.
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param data
	 *            The default minimum and maximum number of data items for all
	 *            sets (can be overridden when adding a set)
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If <code>args</code> is <code>null</code> - or if the data
	 *             range value doesn't make sense
	 */

	public Options(String args[], int data) {
		this(args, Prefix.DASH, Multiplicity.ONCE, data, data);
	}

	/**
	 * Constructor. The prefix is set to {@link Prefix#DASH}, and the
	 * multiplicity is set to {@link Multiplicity#ONCE}.
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param defMinData
	 *            The default minimum number of data items for all sets (can be
	 *            overridden when adding a set)
	 * @param defMaxData
	 *            The default maximum number of data items for all sets (can be
	 *            overridden when adding a set)
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If <code>args</code> is <code>null</code> - or if the data
	 *             range values don't make sense
	 */

	public Options(String args[], int defMinData, int defMaxData) {
		this(args, Prefix.DASH, Multiplicity.ONCE, defMinData, defMaxData);
	}

	/**
	 * Constructor. The default number of data items is set to 0, and the
	 * multiplicity is set to {@link Multiplicity#ONCE}.
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param prefix
	 *            The prefix to use for all command line options. It can only be
	 *            set here for all options at the same time
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If either <code>args</code> or <code>prefix</code> is
	 *             <code>null</code>
	 */

	public Options(String args[], Prefix prefix) {
		this(args, prefix, Multiplicity.ONCE, 0, 0);
	}

	/**
	 * Constructor. The multiplicity is set to {@link Multiplicity#ONCE}.
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param prefix
	 *            The prefix to use for all command line options. It can only be
	 *            set here for all options at
	 * @param data
	 *            The default minimum and maximum number of data items for all
	 *            sets (can be overridden when adding a set)
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If either <code>args</code> or <code>prefix</code> is
	 *             <code>null</code> - or if the data range value doesn't make
	 *             sense
	 */

	public Options(String args[], Prefix prefix, int data) {
		this(args, prefix, Multiplicity.ONCE, data, data);
	}

	/**
	 * Constructor. The multiplicity is set to {@link Multiplicity#ONCE}.
	 * <p>
	 * 
	 * @param args
	 *            The command line arguments to check
	 * @param prefix
	 *            The prefix to use for all command line options. It can only be
	 *            set here for all options at the same time
	 * @param defMinData
	 *            The default minimum number of data items for all sets (can be
	 *            overridden when adding a set)
	 * @param defMaxData
	 *            The default maximum number of data items for all sets (can be
	 *            overridden when adding a set)
	 *            <p>
	 * @throws IllegalArgumentException
	 *             If either <code>args</code> or <code>prefix</code> is
	 *             <code>null</code> - or if the data range values don't make
	 *             sense
	 */

	public Options(String args[], Prefix prefix, int defMinData, int defMaxData) {
		this(args, prefix, Multiplicity.ONCE, defMinData, defMaxData);
	}

	/**
	 * Return the (first) matching set. This invocation does not ignore
	 * unmatched options and requires that data items are the last ones on the
	 * command line.
	 * <p>
	 * 
	 * @return The first set which matches (i. e. the <code>check()</code>
	 *         method returns <code>true</code>) - or <code>null</code>, if no
	 *         set matches.
	 */

	public OptionSet getMatchingSet() {
		return getMatchingSet(false, true);
	}

	/**
	 * Return the (first) matching set.
	 * <p>
	 * 
	 * @param ignoreUnmatched
	 *            A boolean to select whether unmatched options can be ignored
	 *            in the checks or not
	 * @param requireDataLast
	 *            A boolean to indicate whether the data items have to be the
	 *            last ones on the command line or not
	 *            <p>
	 * @return The first set which matches (i. e. the <code>check()</code>
	 *         method returns <code>true</code>) - or <code>null</code>, if no
	 *         set matches.
	 */

	public OptionSet getMatchingSet(boolean ignoreUnmatched, boolean requireDataLast) {
		for (Iterator i = optionSets.keySet().iterator(); i.hasNext();) {
			String setName = i.next().toString();
			if (check(setName, ignoreUnmatched, requireDataLast)) {
				return (OptionSet) optionSets.get(setName);
			}
		}
		return null;
	}

	/**
	 * Add an option set.
	 * <p>
	 * 
	 * @param setName
	 *            The name for the set. This must be a unique identifier
	 * @param minData
	 *            The minimum number of data items for this set
	 * @param maxData
	 *            The maximum number of data items for this set
	 *            <p>
	 * @return The new <code>Optionset</code> instance created. This is useful
	 *         to allow chaining of <code>addOption()</code> calls right after
	 *         this method
	 */

	public OptionSet addSet(String setName, int minData, int maxData) {
		if (setName == null) {
			throw new IllegalArgumentException(CLASS + ": setName may not be null");
		}
		if (optionSets.containsKey(setName)) {
			throw new IllegalArgumentException(CLASS + ": a set with the name " + setName + " has already been defined");
		}
		OptionSet os = new OptionSet(prefix, defaultMultiplicity, setName, minData, maxData);
		optionSets.put(setName, os);
		return os;
	}

	/**
	 * Add an option set.
	 * <p>
	 * 
	 * @param setName
	 *            The name for the set. This must be a unique identifier
	 * @param data
	 *            The minimum and maximum number of data items for this set
	 *            <p>
	 * @return The new <code>Optionset</code> instance created. This is useful
	 *         to allow chaining of <code>addOption()</code> calls right after
	 *         this method
	 */

	public OptionSet addSet(String setName, int data) {
		return addSet(setName, data, data);
	}

	/**
	 * Add an option set. The defaults for the number of data items are used.
	 * <p>
	 * 
	 * @param setName
	 *            The name for the set. This must be a unique identifier
	 *            <p>
	 * @return The new <code>Optionset</code> instance created. This is useful
	 *         to allow chaining of <code>addOption()</code> calls right after
	 *         this method
	 */

	public OptionSet addSet(String setName) {
		return addSet(setName, defaultMinData, defaultMaxData);
	}

	/**
	 * Return an option set - or <code>null</code>, if no set with the given
	 * name exists
	 * <p>
	 * 
	 * @param setName
	 *            The name for the set to retrieve
	 *            <p>
	 * @return The set to retrieve (or <code>null</code>, if no set with the
	 *         given name exists)
	 */

	public OptionSet getSet(String setName) {
		return (OptionSet) optionSets.get(setName);
	}

	/**
	 * This returns the (anonymous) default set
	 * <p>
	 * 
	 * @return The default set
	 */

	public OptionSet getSet() {
		if (getSet(DEFAULT_SET) == null) {
			addSet(DEFAULT_SET, defaultMinData, defaultMaxData);
		}
		return getSet(DEFAULT_SET);
	}

	/**
	 * The error messages collected during the last option check (invocation of
	 * any of the <code>check()</code> methods). This is useful to determine
	 * what was wrong with the command line arguments provided
	 * <p>
	 * 
	 * @return A string with all collected error messages
	 */

	public String getCheckErrors() {
		return checkErrors.toString();
	}

	/**
	 * Run the checks for the default set. <code>ignoreUnmatched</code> is set
	 * to <code>false</code>, and <code>requireDataLast</code> is set to
	 * <code>true</code>.
	 * <p>
	 * 
	 * @return A boolean indicating whether all checks were successful or not
	 */

	public boolean check() {
		return check(DEFAULT_SET, false, true);
	}

	/**
	 * Run the checks for the default set.
	 * <p>
	 * 
	 * @param ignoreUnmatched
	 *            A boolean to select whether unmatched options can be ignored
	 *            in the checks or not
	 * @param requireDataLast
	 *            A boolean to indicate whether the data items have to be the
	 *            last ones on the command line or not
	 *            <p>
	 * @return A boolean indicating whether all checks were successful or not
	 */

	public boolean check(boolean ignoreUnmatched, boolean requireDataLast) {
		return check(DEFAULT_SET, ignoreUnmatched, requireDataLast);
	}

	/**
	 * Run the checks for the given set. <code>ignoreUnmatched</code> is set to
	 * <code>false</code>, and <code>requireDataLast</code> is set to
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param setName
	 *            The name for the set to check
	 *            <p>
	 * @return A boolean indicating whether all checks were successful or not
	 *         <p>
	 * @throws IllegalArgumentException
	 *             If either <code>setName</code> is <code>null</code>, or the
	 *             set is unknown.
	 */

	public boolean check(String setName) {
		return check(setName, false, true);
	}

	/**
	 * Run the checks for the given set.
	 * <p>
	 * 
	 * @param setName
	 *            The name for the set to check
	 * @param ignoreUnmatched
	 *            A boolean to select whether unmatched options can be ignored
	 *            in the checks or not
	 * @param requireDataLast
	 *            A boolean to indicate whether the data items have to be the
	 *            last ones on the command line or not
	 *            <p>
	 * @return A boolean indicating whether all checks were successful or not
	 *         <p>
	 * @throws IllegalArgumentException
	 *             If either <code>setName</code> is <code>null</code>, or the
	 *             set is unknown.
	 */

	public boolean check(String setName, boolean ignoreUnmatched, boolean requireDataLast) {

		if (setName == null) {
			throw new IllegalArgumentException(CLASS + ": setName may not be null");
		}
		if (optionSets.get(setName) == null) {
			throw new IllegalArgumentException(CLASS + ": Unknown OptionSet: " + setName);
		}

		checkErrors = new StringBuffer();
		checkErrors.append("Checking set ");
		checkErrors.append(setName);
		checkErrors.append('\n');

		// .... Access the data for the set to use

		OptionSet set = (OptionSet) optionSets.get(setName);
		java.util.ArrayList options = set.getOptionData();
		java.util.ArrayList data = set.getData();
		java.util.ArrayList unmatched = set.getUnmatched();

		// .... Catch some trivial cases

		if (options.size() == 0) { // No options have been defined at all
			if (arguments.length == 0) { // No arguments have been given: in
				// this case, this is a success
				return true;
			} else {
				checkErrors.append("No options have been defined, nothing to check\n");
				return false;
			}
		} else if (arguments.length == 0) { // Options have been defined, but no
			// arguments given
			checkErrors.append("Options have been defined, but no arguments have been given; nothing to check\n");
			return false;
		}

		// .... Parse all the arguments given

		int ipos = 0;
		int offset = 0;
		java.util.regex.Matcher m = null;
		String value = null;
		String detail = null;
		String next = null;
		String key = null;
		String pre = Character.toString(prefix.getName());
		boolean add = true;
		boolean[] matched = new boolean[arguments.length];

		for (int i = 0; i < matched.length; i++) { // Initially, we assume
			// there was no match at all
			matched[i] = false;
		}

		while (true) {

			value = null;
			detail = null;
			offset = 0;
			add = true;
			key = arguments[ipos];

			for (Iterator i = options.iterator(); i.hasNext();) { // For each
				// argument,
				// we may
				// need to
				// check all
				// defined
				// options
				OptionData optionData = (OptionData) i.next();
				m = optionData.getPattern().matcher(key);
				if (m.lookingAt()) {
					if (optionData.useValue()) { // The code section for
						// value options
						if (optionData.useDetail()) {
							detail = m.group(1);
							offset = 2; // required for correct Matcher.group
							// access below
						}
						if (optionData.getSeparator() == Separator.BLANK) { // In
							// this
							// case,
							// the
							// next
							// argument
							// must
							// be
							// the
							// value
							if (ipos + 1 == arguments.length) { // The last
								// argument,
								// thus no value
								// follows it:
								// Error
								checkErrors.append("At end of arguments - no value found following argument ");
								checkErrors.append(key);
								checkErrors.append('\n');
								add = false;
							} else {
								next = arguments[ipos + 1];
								if (next.startsWith(pre)) { // The next one is
									// an argument, not
									// a value: Error
									checkErrors.append("No value found following argument ");
									checkErrors.append(key);
									checkErrors.append('\n');
									add = false;
								} else {
									value = next;
									matched[ipos++] = true; // Mark the key and
									// the value
									matched[ipos] = true;
								}
							}
						} else { // The value follows the separator in this
							// case
							value = m.group(1 + offset);
							matched[ipos] = true;
						}
					} else { // Simple, non-value options
						matched[ipos] = true;
					}

					if (add) {
						optionData.addResult(value, detail); // Store the
						// result
					}
					break; // No need to check more options, we have a match
				}
			}

			ipos++; // Advance to the next argument to check
			if (ipos >= arguments.length) {
				break; // Terminating condition for the check loop
			}

		}

		// .... Identify unmatched arguments and actual (non-option) data

		int first = -1; // Required later for requireDataLast
		for (int i = 0; i < matched.length; i++) { // Assemble the list of
			// unmatched options
			if (!matched[i]) {
				if (arguments[i].startsWith(pre)) { // This is an unmatched
					// option
					unmatched.add(arguments[i]);
					checkErrors.append("No matching option found for argument ");
					checkErrors.append(arguments[i]);
					checkErrors.append('\n');
				} else { // This is actual data
					if (first < 0) {
						first = i;
					}
					data.add(arguments[i]);
				}
			}
		}

		// .... Checks to determine overall success; start with multiplicity of
		// options

		boolean err = true;

		for (Iterator i = options.iterator(); i.hasNext();) {
			OptionData optionData = (OptionData) i.next();
			key = optionData.getKey();
			err = false; // Local check result for one option

			if ((optionData.getMultiplicity() == Multiplicity.ONCE && optionData.getResultCount() != 1)
					|| (optionData.getMultiplicity() == Multiplicity.ONCE_OR_MORE && optionData.getResultCount() == 0)
					|| (optionData.getMultiplicity() == Multiplicity.ZERO_OR_ONE && optionData.getResultCount() > 1)) {
				err = true;
			}

			if (err) {
				checkErrors.append("Wrong number of occurences found for argument ");
				checkErrors.append(prefix.getName());
				checkErrors.append(key);
				checkErrors.append('\n');
				return false;
			}

		}

		// .... Check range for data

		if (data.size() < set.getMinData() || data.size() > set.getMaxData()) {
			checkErrors.append("Invalid number of data arguments: ");
			checkErrors.append(data.size());
			checkErrors.append(" (allowed range: ");
			checkErrors.append(set.getMinData());
			checkErrors.append(" ... ");
			checkErrors.append(set.getMaxData());
			checkErrors.append(")\n");
			return false;
		}

		// .... Check for location of the data in the list of command line
		// arguments

		if (requireDataLast && first >= 0) {
			if (first + data.size() != arguments.length) {
				checkErrors.append("Invalid data specification: data arguments are not the last ones on the command line\n");
				return false;
			}
		}

		// .... Check for unmatched arguments

		if (!ignoreUnmatched && unmatched.size() > 0) {
			return false; // Don't accept unmatched arguments
		}

		// .... If we made it to here, all checks were successful

		return true;

	}

	/**
	 * Add the given non-value option to <i>all</i> known sets. See
	 * {@link OptionSet#addOption(String)} for details.
	 */

	public void addOptionAllSets(String key) {
		for (Iterator i = optionSets.keySet().iterator(); i.hasNext();) {
			((OptionSet) optionSets.get(i.next())).addOption(key, defaultMultiplicity);
		}
	}

	/**
	 * Add the given non-value option to <i>all</i> known sets. See
	 * {@link OptionSet#addOption(String, Options.Multiplicity)} for details.
	 */

	public void addOptionAllSets(String key, Multiplicity multiplicity) {
		for (Iterator i = optionSets.keySet().iterator(); i.hasNext();) {
			((OptionSet) optionSets.get(i.next())).addOption(key, false, Separator.NONE, false, multiplicity);
		}
	}

	/**
	 * Add the given value option to <i>all</i> known sets. See
	 * {@link OptionSet#addOption(String, Options.Separator)} for details.
	 */

	public void addOptionAllSets(String key, Separator separator) {
		for (Iterator i = optionSets.keySet().iterator(); i.hasNext();) {
			((OptionSet) optionSets.get(i.next())).addOption(key, false, separator, true, defaultMultiplicity);
		}
	}

	/**
	 * Add the given value option to <i>all</i> known sets. See
	 * {@link OptionSet#addOption(String, Options.Separator, Options.Multiplicity)}
	 * for details.
	 */

	public void addOptionAllSets(String key, Separator separator, Multiplicity multiplicity) {
		for (Iterator i = optionSets.keySet().iterator(); i.hasNext();) {
			((OptionSet) optionSets.get(i.next())).addOption(key, false, separator, true, multiplicity);
		}
	}

	/**
	 * Add the given value option to <i>all</i> known sets. See
	 * {@link OptionSet#addOption(String, boolean, Options.Separator)} for
	 * details.
	 */

	public void addOptionAllSets(String key, boolean details, Separator separator) {
		for (Iterator i = optionSets.keySet().iterator(); i.hasNext();) {
			((OptionSet) optionSets.get(i.next())).addOption(key, details, separator, true, defaultMultiplicity);
		}
	}

	/**
	 * Add the given value option to <i>all</i> known sets. See
	 * {@link OptionSet#addOption(String, boolean, Options.Separator, Options.Multiplicity)}
	 * for details.
	 */

	public void addOptionAllSets(String key, boolean details, Separator separator, Multiplicity multiplicity) {
		for (Iterator i = optionSets.keySet().iterator(); i.hasNext();) {
			((OptionSet) optionSets.get(i.next())).addOption(key, details, separator, true, multiplicity);
		}
	}

	/**
	 * This is the overloaded {@link Object#toString()} method, and it is
	 * provided mainly for debugging purposes.
	 * <p>
	 * 
	 * @return A string representing the instance
	 */

	public String toString() {

		StringBuffer sb = new StringBuffer();

		for (Iterator i = optionSets.values().iterator(); i.hasNext();) {
			OptionSet set = (OptionSet) i.next();
			sb.append("Set: ");
			sb.append(set.getSetName());
			sb.append('\n');
			for (Iterator j = set.getOptionData().iterator(); j.hasNext();) {
				OptionData data = (OptionData) j.next();
				sb.append(data.toString());
				sb.append('\n');
			}
		}

		return sb.toString();

	}

}
