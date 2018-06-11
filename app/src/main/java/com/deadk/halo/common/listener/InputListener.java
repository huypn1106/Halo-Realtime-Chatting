package com.deadk.halo.common.listener;

/**
 * Interface definition for a callback to be invoked when user pressed 'submit' button
 */
public interface InputListener {

    /**
     * Fires when user presses 'send' button.
     *
     * @param input input entered by user
     * @return if input text is valid, you must return {@code true} and input will be cleared, otherwise return false.
     */
    boolean onSubmit(CharSequence input);
}
