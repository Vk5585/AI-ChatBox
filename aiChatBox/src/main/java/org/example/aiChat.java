package org.example;

/*
 this only answer for
1. hi
2. what is java swing?
3. tell me java basics?
*/

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class aiChat<API> extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private OpenAiService openAiService;

    // Put your real API key here
    private static final String API_KEY = "OPENAI_API_KEY";

    public aiChat() {
        setTitle("AI ChatBox");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        initAI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(Color.lightGray);
        chatArea.setForeground(Color.BLACK);
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Chat"));

        inputField = new JTextField();
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(this::sendMessage);
        inputField.addActionListener(this::sendMessage);

        JPanel bottom = new JPanel(new BorderLayout(5, 0));
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void initAI() {
        openAiService = new OpenAiService(API_KEY);
    }

    private void sendMessage(ActionEvent e) {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        append("You [" + timeNow() + "]: " + text);
        inputField.setText("");

        new Thread(() -> {
            try {
                String reply = askAI(text);
                SwingUtilities.invokeLater(() ->
                        append("AI [" + timeNow() + "]: " + reply));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        append("AI: Error - " + ex.getMessage()));
            }
        }).start();
    }

    private String askAI(String prompt) {
        String p = prompt.toLowerCase().trim();

        if (p.equals("hi") || p.equals("hello")) {
            return "Hi! I am your offline Java chat bot. Ask me anything about Java or your projects.";
        } else if (p.contains("java swing")) {
            return "Java Swing is a GUI toolkit in Java. You build windows using JFrame, add components like JButton, JTextField, and handle events with ActionListener.";
        } else if (p.contains("java basics")) {
            return "Java basics include variables, data types, if/else, loops, methods, classes, and objects. Practice small programs every day.";
        } else if (p.contains("chat application")) {
            return "A chat application usually has a client and server. Clients send messages to the server, which broadcasts them to other clients over sockets.";
        } else if (p.contains("project idea")) {
            return "Good Java project ideas: ToDo app, calculator, notes app, quiz app, and your current AI ChatBox with more features.";
        }

        // default
        return "You said: " + prompt + " I am a simple offline bot, so I can only answer basic programmed questions.";
    }

    private void append(String msg) {
        chatArea.append(msg + "\n\n");
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private String timeNow() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void addWelcomeMessage() {
        append("AI: Hi! I am your Java AI ChatBox. Type a message below and press Enter.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new aiChat().setVisible(true));
    }

}
