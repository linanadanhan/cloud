/**
 *
 */
package com.gsoft.cos3.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author shencq
 */
public class Assert extends org.springframework.util.Assert {
    /**
     * 判断字符串是否为空.
     *
     * @param str
     * @return Boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || ("".equals(str));
    }

    /**
     * 判断对象是否为空.
     *
     * @param obj
     * @return Boolean
     */
    public static boolean isEmpty(Object obj) {
        if (obj instanceof String) {
            return obj == null || ("".equals(obj));
        } else if (obj instanceof Collection) {
            return obj == null || ((Collection<?>) obj).isEmpty();
        } else if (obj instanceof Object[]) {
            return obj == null || ((Object[]) obj).length == 0;
        } else if(obj instanceof Map){
        	 return obj == null || ((Map<?,?>) obj).isEmpty();
        }else {
        	 return obj == null;
        }
    }

    /**
     * 判断字符串不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断对象不为空
     *
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * {@link RuntimeException} with the given message.
     *
     * @param message   the identifying message for the {@link RuntimeException} (
     *                  <code>null</code> okay)
     * @param condition condition to be checked
     */
    static public void isTrue(boolean condition, String message) {
        if (!condition)
            fail(message);
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * {@link RuntimeException} without a message.
     *
     * @param condition condition to be checked
     */
    static public void isTrue(boolean condition) {
        isTrue(condition, null);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an
     * {@link RuntimeException} with the given message.
     *
     * @param message   the identifying message for the {@link RuntimeException} (
     *                  <code>null</code> okay)
     * @param condition condition to be checked
     */
    static public void isFalse(boolean condition, String message) {
        isTrue(!condition, message);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an
     * {@link RuntimeException} without a message.
     *
     * @param condition condition to be checked
     */
    static public void isFalse(boolean condition) {
        isFalse(condition, null);
    }

    /**
     * Fails a test with the given message.
     *
     * @param message the identifying message for the {@link RuntimeException} (
     *                <code>null</code> okay)
     * @see RuntimeException
     */
    static public void fail(String message) {
        if (message == null)
            throw new RuntimeException("断言失败，不符合预设逻辑！");
        throw new RuntimeException(message);
    }

    /**
     * Fails a test with no message.
     */
    static public void fail() {
        fail(null);
    }

    /**
     * Asserts that two objects are equal. If they are not, an
     * {@link RuntimeException} is thrown with the given message. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>, they
     * are considered equal.
     *
     * @param expected expected value
     * @param actual   actual value
     * @param message  the identifying message for the {@link RuntimeException} (
     *                 <code>null</code> okay)
     */
    static public void equals(Object expected, Object actual, String message) {
        if (expected == null && actual == null)
            return;
        if (expected != null && isEquals(expected, actual))
            return;
        failNotEquals(message, expected, actual);
    }

    private static boolean isEquals(Object expected, Object actual) {
        return expected.equals(actual);
    }

    /**
     * Asserts that two objects are equal. If they are not, an
     * {@link RuntimeException} without a message is thrown. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>, they
     * are considered equal.
     *
     * @param expected expected value
     * @param actual   the value to check against <code>expected</code>
     */
    static public void equals(Object expected, Object actual) {
        equals(expected, actual, null);
    }

    /**
     * Asserts that two doubles or floats are equal to within a positive delta.
     * If they are not, an {@link RuntimeException} is thrown with the given
     * message. If the expected value is infinity then the delta value is
     * ignored. NaNs are considered equal:
     * <code>assertEquals(Double.NaN, Double.NaN, *)</code> passes
     *
     * @param expected expected value
     * @param actual   the value to check against <code>expected</code>
     * @param delta    the maximum delta between <code>expected</code> and
     *                 <code>actual</code> for which both numbers are still
     *                 considered equal.
     * @param message  the identifying message for the {@link RuntimeException} (
     *                 <code>null</code> okay)
     */
    static public void equals(double expected, double actual, double delta,
                              String message) {
        if (Double.compare(expected, actual) == 0)
            return;
        if (!(Math.abs(expected - actual) <= delta))
            failNotEquals(message, new Double(expected), new Double(actual));
    }

    /**
     * Asserts that two longs are equal. If they are not, an
     * {@link RuntimeException} is thrown.
     *
     * @param expected expected long value.
     * @param actual   actual long value
     */
    static public void equals(long expected, long actual) {
        equals(expected, actual, null);
    }

    /**
     * Asserts that two longs are equal. If they are not, an
     * {@link RuntimeException} is thrown with the given message.
     *
     * @param expected long expected value.
     * @param actual   long actual value
     * @param message  the identifying message for the {@link RuntimeException} (
     *                 <code>null</code> okay)
     */
    static public void equals(long expected, long actual, String message) {
        equals((Long) expected, (Long) actual, message);
    }

    /**
     * Asserts that two doubles or floats are equal to within a positive delta.
     * If they are not, an {@link RuntimeException} is thrown. If the expected
     * value is infinity then the delta value is ignored.NaNs are considered
     * equal: <code>assertEquals(Double.NaN, Double.NaN, *)</code> passes
     *
     * @param expected expected value
     * @param actual   the value to check against <code>expected</code>
     * @param delta    the maximum delta between <code>expected</code> and
     *                 <code>actual</code> for which both numbers are still
     *                 considered equal.
     */
    static public void equals(double expected, double actual, double delta) {
        equals(expected, actual, delta, null);
    }

    /**
     * Asserts that an object isn't null. If it is an {@link RuntimeException}
     * is thrown with the given message.
     *
     * @param object  Object to check or <code>null</code>
     * @param message the identifying message for the {@link RuntimeException} (
     *                <code>null</code> okay)
     */
    static public void notNull(Object object, String message) {
        isTrue(object != null, message);
    }

    /**
     * Asserts that an object isn't null. If it is an {@link RuntimeException}
     * is thrown.
     *
     * @param object Object to check or <code>null</code>
     */
    static public void notNull(Object object) {
        notNull(object, null);
    }

    /**
     * Asserts that an object is null. If it is not, an
     * {@link RuntimeException} is thrown with the given message.
     *
     * @param object  Object to check or <code>null</code>
     * @param message the identifying message for the {@link RuntimeException} (
     *                <code>null</code> okay)
     */
    static public void isNull(Object object, String message) {
        isTrue(object == null, message);
    }

    /**
     * Asserts that an object is null. If it isn't an {@link RuntimeException}
     * is thrown.
     *
     * @param object Object to check or <code>null</code>
     */
    static public void isNull(Object object) {
        isNull(object, null);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not, an
     * {@link RuntimeException} is thrown with the given message.
     *
     * @param expected the expected object
     * @param actual   the object to compare to <code>expected</code>
     * @param message  the identifying message for the {@link RuntimeException} (
     *                 <code>null</code> okay)
     */
    static public void same(Object expected, Object actual, String message) {
        if (expected == actual)
            return;
        failNotSame(message, expected, actual);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not the
     * same, an {@link RuntimeException} without a message is thrown.
     *
     * @param expected the expected object
     * @param actual   the object to compare to <code>expected</code>
     */
    static public void same(Object expected, Object actual) {
        same(expected, actual, null);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object, an {@link RuntimeException} is thrown with the
     * given message.
     *
     * @param unexpected the object you don't expect
     * @param actual     the object to compare to <code>unexpected</code>
     * @param message    the identifying message for the {@link RuntimeException} (
     *                   <code>null</code> okay)
     */
    static public void notSame(Object unexpected, Object actual, String message) {
        if (unexpected == actual)
            failSame(message);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object, an {@link RuntimeException} without a message
     * is thrown.
     *
     * @param unexpected the object you don't expect
     * @param actual     the object to compare to <code>unexpected</code>
     */
    static public void notSame(Object unexpected, Object actual) {
        notSame(unexpected, actual, null);
    }

    static private void failSame(String message) {
        String formatted = "";
        if (message != null)
            formatted = message + " ";
        fail(formatted + "expected not same");
    }

    static private void failNotSame(String message, Object expected,
                                    Object actual) {
        String formatted = "";
        if (message != null)
            formatted = message + " ";
        fail(formatted + "expected same:<" + expected + "> was not:<" + actual
                + ">");
    }

    static private void failNotEquals(String message, Object expected,
                                      Object actual) {
        fail(format(message, expected, actual));
    }

    static private String format(String message, Object expected, Object actual) {
        String formatted = "";
        if (message != null && !message.equals(""))
            formatted = message + " ";
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if (expectedString.equals(actualString))
            return formatted + "expected: "
                    + formatClassAndValue(expected, expectedString)
                    + " but was: " + formatClassAndValue(actual, actualString);
        else
            return formatted + "expected:<" + expectedString + "> but was:<"
                    + actualString + ">";
    }

    private static String formatClassAndValue(Object value, String valueString) {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }

}
