package org.avol.azure.eh.listner;

import com.microsoft.azure.eventprocessorhost.ExceptionReceivedEventArgs;

import java.util.function.Consumer;

public class ErrorNotificationHandler implements Consumer<ExceptionReceivedEventArgs> {
    @Override
    public void accept(ExceptionReceivedEventArgs args) {
        System.out.println("Host: " + args.getHostname()
                + " received Error while " + args.getAction() + ", " + args.getException().getMessage());
    }
}
