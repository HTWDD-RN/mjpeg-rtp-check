import java.lang.Character;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
 
/**
 * Parses command line arguments.
 *
 * The ArgumentParser currently just supports required
 * arguments as Strings and optional arguments in form
 * of switches (boolean values).
 *
 * @author Emanuel Günther (s76954)
 */
public class ArgumentParser {
    private String command;
    private String version;
    private ArrayList<String> arguments;
    private HashMap<String, String> options;
    private HashMap<String, String> shortToLong;
    private HashMap<String, Object> parsed;

    /**
     * Create an ArgumentParser for the given
     * command.
     *
     * @param command command
     */
    public ArgumentParser(String command, String version) {
        this.command = command;
        this.version = version;
        arguments = new ArrayList<String>();
        options = new HashMap<String, String>();
        parsed = new HashMap<String, Object>();
        shortToLong = new HashMap<String, String>();
        registerOption("h", "help", "print this message");
        registerOption("v", "version", "print version and license info");
    }

    /**
     * Return the specified argument as boolean.
     *
     * @param arg argument name
     * @return The argument value as boolean.
     */
    public boolean getBoolean(String arg) throws NoSuchElementException {
        Object ret = parsed.get(arg);
        if (ret == null) {
            throw new NoSuchElementException();
        }
        return (boolean)ret;
    }

    /**
     * Return the specified argument as String.
     *
     * @param arg argument name
     * @return The argument value as String.
     */
    public String getString(String arg) throws NoSuchElementException {
        Object ret = parsed.get(arg);
        if (ret == null) {
            throw new NoSuchElementException();
        }
        return (String)ret;
    }

    /**
     * Parse the given arguments using the previous
     * specified arguments and options.
     *
     * @param args the command line arguments
     * @return true if parsing was successful, false otherwise
     */
    public boolean parse(String[] args) {
        int argIdx = 0;
        for (String s : args) {
            String opt = "";
            boolean shortOpt = false;
            boolean longOpt = false;
            if (s.startsWith("--")) {
                opt = s.substring(2);
                longOpt = true;
            } else if (s.startsWith("-")) {
                opt = s.substring(1);
                shortOpt = true;
            }
            if (shortOpt || longOpt) {
                int idx = 0;
                do {
                    String currOpt = opt;
                    if (shortOpt) {
                        currOpt = Character.toString(currOpt.charAt(idx++));
                    }
                    if (!currOpt.isEmpty() && options.get(currOpt) != null) {
                        String putOpt = currOpt;
                        String stlOpt = shortToLong.get(currOpt);
                        if (stlOpt != null) {
                            putOpt = stlOpt;
                        }
                        parsed.put(putOpt, true);
                    } else {
                        System.out.println("Option was not recognized: " + currOpt);
                        usage();
                        System.exit(1);
                    }
                } while (shortOpt && idx < opt.length());
            } else {
                if (argIdx < arguments.size()) {
                    parsed.put(arguments.get(argIdx++), s);
                } else {
                    System.out.println("Too many arguments: " + s);
                    usage();
                    System.exit(1);
                }
            }
        }

        if ((boolean)parsed.get("help")) {
            usage();
            System.exit(0);
        }

        if ((boolean)parsed.get("version")) {
            version();
            System.exit(0);
        }

        for (String arg : arguments) {
            if (parsed.get(arg) == null) {
                System.out.println("Argument is missing: " + arg);
                usage();
                System.exit(1);
            }
        }
        return true;
    }

    /**
     * Register a required Argument.
     *
     * @param argument the argument name
     * @return true if successful, false otherwise
     */
    public boolean registerArgument(String argument) {
        arguments.add(argument);
        return true;
    }

    /**
     * Register an optional Argument.
     *
     * @param shortOpt short option
     * @param longOpt long option
     * @param help help text
     * @return true if successful, false otherwise
     */
    public boolean registerOption(String shortOpt, String longOpt, String help) {
        if (shortOpt != null) {
            options.put(shortOpt, help);
            if (longOpt == null) { // use short opt id just if no long opt
                parsed.put(shortOpt, false);
            }
        }
        if (longOpt != null) {
            options.put(longOpt, help);
            parsed.put(longOpt, false);
            if (shortOpt != null) {
                shortToLong.put(shortOpt, longOpt);
            }
        }
        return true;
    }

    private void usage() {
        String usage = "usage: " + command;
        if (!options.isEmpty()) {
            usage += " [options]";
        }
        if (!arguments.isEmpty()) {
            for (int i = 0; i < arguments.size(); i++) {
                usage += " " + arguments.get(i);
            }
        }
        System.out.println(usage);

        ArrayList<String> alist = new ArrayList<String>(options.keySet());
        String longest = "";
        for (String current : alist) {
            if (current.length() > longest.length()) {
                longest = current;
            }
        }
        
        int initialOffset = 2; // spaces at beginning of the line
        int helpOffset = initialOffset + longest.length() + 7; // 2 short Option (-v), 2 Dashes, 3 Spaces

        ArrayList<String> optList = new ArrayList<String>(options.keySet());
        optList.sort(null); // use natural ordering
        for (String next : optList) {
            String out = " ".repeat(initialOffset);
            if (next.length() == 1) {
                out += "-" + next;
                String stl = shortToLong.get(next);
                if (stl != null) {
                    out += ",--" + stl;
                }
            } else {
                if (shortToLong.values().contains(next)) {
                    continue;
                } else {
                    out += " ".repeat(3);
                    out += "--" + next;
                }
            }

            out += " ".repeat(helpOffset - out.length());
            out += options.get(next);
            System.out.println(out);
        }
    }

    private void version() {
        String out = command + " " + version + "\n";
        out += "Copyright (C) 2021 HTW-Dresden.\n";
        out += "This is free software distributed under a BSD-2-Clause License.\n";
        out += "For further details see LICENSE in the source distribution or at\n";
        out += "https://opensource.org/licenses/BSD-2-Clause";
        System.out.println(out);
    }
}

