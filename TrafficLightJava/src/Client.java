import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    String msg="";
    private Panel JPanel1;
    private JPanel panel1;
    private JRadioButton mNyala;
    private JRadioButton mMati;
    private JRadioButton kNyala;
    private JRadioButton Kmati;
    private JRadioButton hNyala;
    private JRadioButton hMati;
    private JTextField pClient;
    private JTextField ipClient;
    private final String MERAH = "Merah";
    private final String KUNING = "Kuning";
    private final String HIJAU = "Hijau";
    private final String HIDUP = "Hidup";
    private final String MATI = "Mati";


    public Client()
    {
        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(mNyala);
        bg1.add(mMati);

        ButtonGroup bg2 = new ButtonGroup();
        bg2.add(kNyala);
        bg2.add(Kmati);

        ButtonGroup bg3 = new ButtonGroup();
        bg3.add(hNyala);
        bg3.add(hMati);

        //if(mNyala.isSelected()){
            mNyala.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendData(setMessage(MERAH, HIDUP));
                }
            });
       // }if(mMati.isSelected()){
            mMati.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendData(setMessage(MERAH,MATI));
                }
            });

        //}if(kNyala.isSelected()) {
            kNyala.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendData(setMessage(KUNING,HIDUP));
                }
            });
        //}if(Kmati.isSelected()){
            Kmati.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendData(setMessage(KUNING,MATI));
                }
            });

        //}if(hNyala.isSelected()) {
            hNyala.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendData(setMessage(HIJAU,HIDUP));
                }
            });
        //}if(hMati.isSelected()){
            hMati.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendData(setMessage(HIJAU,MATI));
                }
            });

        //}



        /*cbg = new CheckboxGroup();
        stop = new Checkbox("Stop", cbg, false);
        ready = new Checkbox("Ready", cbg, false);
        go= new Checkbox("Go", cbg, false);
        add(stop);
        add(ready);
        add(go);
        stop.addItemListener(this);
        ready.addItemListener(this);
        go.addItemListener(this);*/
    }

    private String setMessage(String color, String status) {
        return color + " " + status + " ";
    }

    private void sendData(String message) {
        try {
            AtomicInteger messageWritten = new AtomicInteger(0);
            AtomicInteger messageRead = new AtomicInteger(0);

            EchoClient(ipClient.getText(), Integer.parseInt(pClient.getText()), message, messageWritten, messageRead);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void EchoClient(String host, int port, final String message, final AtomicInteger messageWritten, final AtomicInteger messageRead) throws IOException {
        AsynchronousSocketChannel sockChannel = AsynchronousSocketChannel.open();

        //try to connect to the server side
        sockChannel.connect(new InetSocketAddress(host, port), sockChannel, new CompletionHandler<Void, AsynchronousSocketChannel>() {
            @Override
            public void completed(Void result, AsynchronousSocketChannel channel) {
                //start to read message
                startRead(channel, messageRead);

                //write an message to server side
                startWrite(channel, String.valueOf(message), messageWritten);
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println("fail to connect to server");
            }

        });
    }

    private void startRead(AsynchronousSocketChannel channel, AtomicInteger messageRead) {
        final ByteBuffer buf = ByteBuffer.allocate(2048);

        channel.read(buf, channel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                //message is read from server
                messageRead.getAndIncrement();

                System.out.println("Read message: " + new String(buf.array()));
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println("Fail to read message from server");
            }
        });
    }

    private void startWrite(AsynchronousSocketChannel channel, String message, AtomicInteger messageWritten) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        buf.put(message.getBytes());
        buf.flip();
        messageWritten.getAndIncrement();
        channel.write(buf, channel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                //after message written
                //NOTHING TO DO
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println("Fail to write the message to server");
            }
        });
    }

    public static void main(String[] args){
        JFrame gui = new JFrame("Traffic Light Java");
        gui.setContentPane(new Client().panel1);
        gui.setPreferredSize(new Dimension(800, 400));
        gui.setLocationRelativeTo(null);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.pack();
        gui.setVisible(true);
    }

    /*public void itemStateChanged(ItemEvent ie)
    {
        repaint();
    }

    public void paint(Graphics g)
    {

        msg=cbg.getSelectedCheckbox().getLabel();
        g.drawOval(165,40,50,50);
        g.drawOval(165,100,50,50);
        g.drawOval(165,160,50,50);



        if(msg.equals("Stop"))
        {
            g.setColor(Color.red);
            g.fillOval(165,40,50,50);
        }
        else if(msg.equals("Ready"))
        {
            g.setColor(Color.yellow);
            g.fillOval(165,100,50,50);
        }
        else
        {
            g.setColor(Color.green);
            g.fillOval(165,160,50,50);
        }

    }

    public static void main(String[] args) {
        JFrame gui = new JFrame("Client");
        gui.setContentPane(new Client().panel1);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.pack();
        gui.setVisible(true);
        Client c = new Client();
        c.Client();
        c.setVisible(true);
    }*/
}
