package bsu.rfict.group6.volkov.varB.var1;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.AttributeSet;

import javax.swing.text.BadLocationException;
//из чего состоит мас - адресс
//http
//url
@SuppressWarnings("serial")

public class MainFrame extends JFrame
{
    private int selectedPort;
    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;
    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;
    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;
    private static int SERVER_PORT = 4567;


    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;
    private final JTextPane textPaneIncoming;
    private final JTextArea textAreaOutgoing;
    boolean port;

    public MainFrame(int selectedPort) {
        super(FRAME_TITLE);
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));

        // Центрирование окна
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2,
                (kit.getScreenSize().height - getHeight()) / 2);

        // Текстовая область для отображения полученных сообщений
        textPaneIncoming = new JTextPane();
        textPaneIncoming.setEditable(false);

        // Контейнер, обеспечивающий прокрутку текстовой области
        final JScrollPane scrollPaneIncoming = new JScrollPane(textPaneIncoming);

        // Подписи полей
        final JLabel labelFrom = new JLabel("Подпись");
        final JLabel labelTo = new JLabel("Получатель");

        // Поля ввода имени пользователя и адреса получателя
        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);

        // Текстовая область для ввода сообщения
        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);

        // Контейнер, обеспечивающий прокрутку текстовой области
        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);

        // Панель ввода сообщения
        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));

        // Кнопка отправки сообщения
        final JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Компоновка элементов панели "Сообщение"
        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout2.createSequentialGroup()
                                .addComponent(labelFrom)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldFrom)
                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldTo))
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());
        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelFrom)
                        .addComponent(textFieldFrom)
                        .addComponent(labelTo)
                        .addComponent(textFieldTo))
                .addGap(MEDIUM_GAP)
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addComponent(sendButton)
                .addContainerGap());

        // Компоновка элементов фрейма
        final GroupLayout layout1 = new GroupLayout(getContentPane());
        setLayout(layout1);

        layout1.setHorizontalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap());
        layout1.setVerticalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());

        // Создание и запуск потока-обработчика запросов
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();//поток блокируется и ждет, пока не появится новое входящее соединение от клиента.

                        final DataInputStream in = new DataInputStream(socket.getInputStream());

                        //нарисовать модель взаимодейтвия
                        //поднять два приложегря на однои

                        // Читаем имя отправителя
                        final String senderName = in.readUTF();
                        // Читаем сообщение
                        final String message = in.readUTF();
                        // Закрываем соединение
                        socket.close();
                        // Выделяем IP-адрес
                        final String address = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()
                                .getHostAddress();
                        // Выводим сообщение в текстовую область
                        appendToTextPane(textPaneIncoming, senderName + " (" + address + "): " + message + "\n");
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в работе сервера", "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    }
    private void appendToTextPane(JTextPane textPane, String msg) {
        if (msg.contains("<i>") && msg.contains("</i>")) {
            String italicText = msg.substring(msg.indexOf("<i>") + 3, msg.indexOf("</i>"));
            String regularTextBefore = msg.substring(0, msg.indexOf("<i>"));
            String regularTextAfter = msg.substring(msg.indexOf("</i>") + 4);

            SimpleAttributeSet italicStyle = new SimpleAttributeSet();
            StyleConstants.setItalic(italicStyle, true);

            appendStyledText(textPane, regularTextBefore, italicText, regularTextAfter, italicStyle);
        } else if (msg.contains("<b>") && msg.contains("</b>")) {
            String boldText = msg.substring(msg.indexOf("<b>") + 3, msg.indexOf("</b>"));
            String regularTextBefore = msg.substring(0, msg.indexOf("<b>"));
            String regularTextAfter = msg.substring(msg.indexOf("</b>") + 4);

            SimpleAttributeSet boldStyle = new SimpleAttributeSet();
            StyleConstants.setBold(boldStyle, true);

            appendStyledText(textPane, regularTextBefore, boldText, regularTextAfter, boldStyle);
        } else if (msg.contains("<u>") && msg.contains("</u>")) {
            String underlineText = msg.substring(msg.indexOf("<u>") + 3, msg.indexOf("</u>"));
            String regularTextBefore = msg.substring(0, msg.indexOf("<u>"));
            String regularTextAfter = msg.substring(msg.indexOf("</u>") + 4);

            SimpleAttributeSet underlineStyle = new SimpleAttributeSet();
            StyleConstants.setUnderline(underlineStyle, true);

            appendStyledText(textPane, regularTextBefore, underlineText, regularTextAfter, underlineStyle);
        } else {
            appendStyledText(textPane, msg, "", "", null);
        }
    }

    private void appendStyledText(JTextPane textPane, String regularTextBefore, String styledText, String regularTextAfter, AttributeSet style) {
        try {
            textPane.getDocument().insertString(textPane.getDocument().getLength(), regularTextBefore, null);
            textPane.getDocument().insertString(textPane.getDocument().getLength(), styledText, style);
            textPane.getDocument().insertString(textPane.getDocument().getLength(), regularTextAfter, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    private void sendMessage() {
        try {
            // Получаем необходимые параметры
            final String senderName = textFieldFrom.getText();
            final String destinationAddress = textFieldTo.getText();
            final String message = textAreaOutgoing.getText();
            // Убеждаемся, что поля не пустые
            if (senderName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите имя отправителя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (destinationAddress.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите адрес узла-получателя", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Создаем сокет для соединения
            final Socket socket =
                    new Socket(destinationAddress, SERVER_PORT);
            // Открываем поток вывода данных
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            // Записываем в поток имя
            out.writeUTF(senderName);
            // Записываем в поток сообщение
            out.writeUTF(message);
            // Закрываем сокет
            socket.close();
            // Помещаем сообщения в текстовую область вывода
            appendToTextPane(textPaneIncoming, "Я -> " + destinationAddress + ": " + message + "\n");
            // Очищаем текстовую область ввода сообщения
            textAreaOutgoing.setText("");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "Не удалось отправить сообщение: узел-адресат не найден",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "Не удалось отправить сообщение", "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override

            public void run()
            {
                final MainFrame frame = new MainFrame(SERVER_PORT);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
