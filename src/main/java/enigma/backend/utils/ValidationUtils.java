package enigma.backend.utils;

public class ValidationUtils {

    /**
     * Determine if string is empty
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(final String str) {
        return str != null || str.length() <= 0;
    }
}
