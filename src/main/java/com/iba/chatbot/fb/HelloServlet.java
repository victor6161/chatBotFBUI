package com.iba.chatbot.fb;

import com.restfb.*;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.send.SendResponse;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
@WebServlet("/Webhook")
public class HelloServlet extends HttpServlet {
    public HelloServlet() {
    }

    private static final String ACCESS_TOKEN = "EAABw8XJo3pYBABRZBuD40v4eUv5JAWZC1ZAWx7WhQoajxyTWGxjYfMiXFJZBuymZAJueSLbpngpLt7uNFlwsL6DDJ0RMbFHmRJyUqoqs7ZAXrfhahW8aMaeGnjyLUXZAQkTvP8HEzs5E59TRH5EoXanOGqBLGpZBNRpNQtcgJvxDhmxduttw2sGO";

    private static final String VERIFY_TOKEN = "yvkmv";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String hubToken = request.getParameter("hub.verify_token");
        String hubChallenge = request.getParameter("hub.challenge");

        if(VERIFY_TOKEN.equals(hubToken)) {
            response.getWriter().write(hubChallenge);
            response.getWriter().flush();
            response.getWriter().close();
        } else {
            response.getWriter().write("incorrect token");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = request.getReader();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        JsonMapper mapper = new DefaultJsonMapper();
        WebhookObject webhookObject = mapper.toJavaObject(sb.toString(), WebhookObject.class);

        for(WebhookEntry entry: webhookObject.getEntryList()) {
            if(entry.getMessaging() != null) {
                for(MessagingItem messagingItem : entry.getMessaging()) {

                    String senderId = messagingItem.getSender().getId();
                    IdMessageRecipient recipient = new IdMessageRecipient(senderId);

                    if(messagingItem.getMessage() != null) {
                        sendMessage(recipient, new Message("Hi"));

                    }
                }
            }
        }

    }

    void sendMessage(IdMessageRecipient recipient, Message message) {
        String pageAccessToken = "MY PAGE ACCESS TOKEN";
        FacebookClient pageClient = new DefaultFacebookClient(ACCESS_TOKEN, Version.VERSION_2_12);

        SendResponse resp = pageClient.publish("me/messages", SendResponse.class,
                Parameter.with("recipient", recipient), // the id or phone recipient
                Parameter.with("message", message)); // one of the messages from above
    }

}
