import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Server extends Canvas{
    public JTextField textWarna;
    private JPanel Oval;
    JPanel server;
    private JTextField portServer;
    private JTextField ipServer;
    private JButton bListen;
    private JPanel panelMerah;
    private JPanel panelKuning;
    private JPanel panelHijau;
    private final String MERAH = "Merah";
    private final String KUNING = "Kuning";
    private final String HIJAU = "Hijau";
    private final String HIDUP = "Hidup";
    private final String MATI = "Mati";
    private Color onColor;                // color on
    private Color offColor = Color.black; // color off
    private Color currentColor;           // color the lens is now
    private final static int SIZE = 100;  // how big is this Lens?
    private final static int OFFSET = 20; // offset of Lens in Canvas

    public Server(){
        bListen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    new Thread(() -> {
                       try {
                           EchoServer(ipServer.getText(), Integer.parseInt(portServer.getText()));
                       } catch (IOException ioException){
                           ioException.printStackTrace();
                       }
                    }).start();
                } catch (Exception ex){
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
    }

    private Color chooseColor(String choose) {
        return switch (choose) {
            case MERAH -> Color.RED;
            case KUNING -> Color.YELLOW;
            case HIJAU -> Color.GREEN;
            default -> throw new IllegalStateException("Unexpected value: " + choose);
        };
    }

    public static void main(String[] args) {
        JFrame gui = new JFrame("Traffic Light Java");
        gui.setContentPane(new Server().server);
        gui.setPreferredSize(new Dimension(700, 400));
        gui.setLocationRelativeTo(null);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.pack();
        gui.setVisible(true);
    }

    public void paint( Graphics g )
    {
        g.setColor( this.currentColor );
        g.fillOval( OFFSET, OFFSET,
                SIZE - OFFSET*2, SIZE - OFFSET*2 );
    }
    /**
     * Have this Lens display its color.
     */
    public void turnOn()
    {
        currentColor = onColor;
        this.repaint();
    }
    /**
     * Darken this lens.
     */
    public void turnOff()
    {
        currentColor = offColor;
        this.repaint();
    }
    public void setTextWarna(String s){
        textWarna.setText(s);
    }


    private void EchoServer(String address, int port) throws IOException {
        InetSocketAddress sockAddr = new InetSocketAddress(address, port);

        //create a socket channel and bind to local bind address
        AsynchronousServerSocketChannel serverSock = AsynchronousServerSocketChannel.open().bind(sockAddr);

        //start to accept the connection from client
        serverSock.accept(serverSock, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {

            @Override
            public void completed(AsynchronousSocketChannel sockChannel, AsynchronousServerSocketChannel serverSock) {
                //a connection is accepted, start to accept next connection
                serverSock.accept(serverSock, this);
                //start to read message from the client
                startRead(sockChannel);
            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel serverSock) {
                System.out.println("fail to accept a connection");
            }

        });
    }

    private void startRead(AsynchronousSocketChannel sockChannel) {
        final ByteBuffer buf = ByteBuffer.allocate(2048);

        //read message from client
        sockChannel.read(buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {

            /**
             * some message is read from client, this callback will be called
             */
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                buf.flip();

                // echo the message
                startWrite(channel, buf);

                //start to read next message again
                startRead(channel);

                // convert and display
                String bufArray = new String(buf.array());
                String[] receiveData = bufArray.split(" ");


                //Color color = new Color(red, green, blue);
                //panelBackgroundColor.setBackground(color);
                Color colorData = Color.BLACK;
                if (receiveData[1].equals(HIDUP)) {
                    colorData = chooseColor(receiveData[0]);
                }
                switch (receiveData[0]) {
                    case MERAH -> panelMerah.setBackground(colorData);
                    case KUNING -> panelKuning.setBackground(colorData);
                    case HIJAU -> panelHijau.setBackground(colorData);
                }
                textWarna.setText(" ");
                textWarna.setText("Lampu " + receiveData[0] + " " + receiveData[1]);
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println("fail to read message from client");
            }
        });
    }

    private void startWrite(AsynchronousSocketChannel channel, ByteBuffer buf) {
        channel.write(buf, channel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {

            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                //finish to write message to client, nothing to do
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                //fail to write message to client
                System.out.println("Fail to write message to client");
            }

        });
    }
}
