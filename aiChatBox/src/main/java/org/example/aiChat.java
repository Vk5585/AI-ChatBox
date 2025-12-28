package org.example;

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

public class aiChat extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private OpenAiService openAiService;
    private String apiKey;  

    public aiChat() {
        setTitle("AI ChatBox - Secure OpenAI Integration");
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
        chatArea.setBackground(Color.decode("#F0F8FF"));  // Light blue
        chatArea.setForeground(Color.BLACK);
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("AI Chat"));

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
        apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "❌ OPENAI_API_KEY environment variable missing!\n\n" +
                "Setup:\n" +
                "1. Run → Edit Configurations\n" +
                "2. Environment variables → +\n" +
                "3. Name: OPENAI_API_KEY\n" +
                "4. Value: sk-your-new-key-from-openai.com\n" +
                "5. Apply & Run",
                "API Key Required", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            openAiService = new OpenAiService(apiKey);
            addWelcomeMessage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "❌ OpenAI Service Error: " + e.getMessage(),
                "Initialization Failed", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
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
                    append("🤖 AI [" + timeNow() + "]: " + reply));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                    append("❌ AI Error: " + ex.getMessage()));
            }
        }).start();
    }

    private String askAI(String prompt) {
        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                .maxTokens(200)
                .temperature(0.7)
                .build();

            return openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            return "API Error: " + e.getMessage() + 
                   "\n\nCheck your API key and internet connection.";
        }
    }

    private void append(String msg) {
        chatArea.append(msg + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private String timeNow() {
        return LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private void addWelcomeMessage() {
        append("🤖 AI: Hi! I'm your secure OpenAI-powered chat assistant. Ask me anything about Java, programming, projects, or general questions!");
        append("💡 Tip: Check Run Configurations → Environment variables for OPENAI_API_KEY");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new aiChat().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
