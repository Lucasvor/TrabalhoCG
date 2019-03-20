import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import javax.swing.*;

// Esta é uma ferramenta gráfica de desenho básica no qual se podem criar figuras simples.

public class Desenhando{

    public static void main(String args[]){

        JanelaComandos JanelaComandos = new JanelaComandos();

        JanelaComandos.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JanelaComandos.setSize(1000,500);
        JanelaComandos.setVisible(true);
        JanelaComandos.setLocationRelativeTo(null);

    }
}

// Autor: João Matheus Santos Assis

class JanelaComandos extends JFrame{

    // Barra de Ferramenta com os botões necessários
    private JToolBar Barra_De_Ferramenta = new JToolBar();
    private JButton Retangulo = new JButton("Retângulo");
    private JButton Circulo = new JButton("Circulo");
    private JButton Reta = new JButton("Reta");
    private JButton Editar_Cor = new JButton("Editar Cor");
    private JButton LimpaTela = new JButton("Limpa Tela");
    private JPanel panel = new JPanel(new BorderLayout());



    private Gerador_Desenho Panel_Desenho = new Gerador_Desenho();

    private Color Cor_da_Figura = Color.RED;
    private Color Ultima_Cor = Color.RED;


    private JLabel Status = new JLabel("  Forma ativa: Retângulo");
    private JLabel Posicao = new JLabel("Posição:      ");

    private JRadioButton Bresenham = new JRadioButton("Bresenham",true);
    private JRadioButton DDA = new JRadioButton("DDA");
    private ButtonGroup bg = new ButtonGroup();


    public JanelaComandos() {
        super("Desenvolvedor Gráfico");


        bg.add(Bresenham);
        bg.add(DDA);


        panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        panel.add(Status,BorderLayout.WEST);
        panel.add(Posicao,BorderLayout.EAST);
        Panel_Desenho.getPanel(Posicao);

        // Adicionando os componentes
        Barra_De_Ferramenta.add(Retangulo);
        Barra_De_Ferramenta.add(Circulo);
        Barra_De_Ferramenta.add(Reta);
        Barra_De_Ferramenta.add(Editar_Cor);
        Barra_De_Ferramenta.add(LimpaTela);
        Barra_De_Ferramenta.add(Bresenham);
        Barra_De_Ferramenta.add(DDA);


        // Cor de fundo da Barra de Status
        Status.setBackground(Color.WHITE);

        add(Barra_De_Ferramenta, BorderLayout.NORTH);
        add(Panel_Desenho, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
       // add(Posicao, BorderLayout.SOUTH);

        Eventos_Desenhando Eventos = new Eventos_Desenhando();
        Retangulo.addActionListener(Eventos);
        Circulo.addActionListener(Eventos);
        Reta.addActionListener(Eventos);
        Editar_Cor.addActionListener(Eventos);
        LimpaTela.addActionListener(Eventos);
        Bresenham.addActionListener(Eventos);
        DDA.addActionListener(Eventos);


    }



    private class Eventos_Desenhando implements ActionListener{

        /* A variável Forma determinará qual figura deverá ser desenhada,
         se seu valor for 0 será desenhado um Retângulo, caso for 1
         um Círculo, 2 será uma reta*/
        int Forma = 0;


        public void actionPerformed(ActionEvent event) {


            if (event.getSource()== Retangulo){
                Forma=0;
                Status.setText("  Forma ativa: Retângulo");
                repaint();
            }

            if (event.getSource() == Circulo){
                Forma=1;
                Status.setText("  Forma ativa: Circulo");
                repaint();
            }

            if (event.getSource() == Reta){
                Forma=2;
                Status.setText("  Forma ativa: Reta");
                repaint();
            }
            if (event.getSource() == LimpaTela) {
                Panel_Desenho.setBuffered_da_Imagem();
            }
            if(event.getSource() == Bresenham){
                Panel_Desenho.setTipo_Algoritmo(true);
            }
            if(event.getSource() == DDA){
                Panel_Desenho.setTipo_Algoritmo(false);
            }

            // Criando uma Caixa de Cor para mudar a cor da linha
            if (event.getSource() == Editar_Cor){
                Cor_da_Figura = JColorChooser.showDialog(JanelaComandos.this,
                        "Editando Cor da Figura", Cor_da_Figura);
                if (Cor_da_Figura != null){
                    Ultima_Cor = Cor_da_Figura;
                }
            }
            // Enviando a Forma a ser desenhada e a cor da linha
            Panel_Desenho.setForma( Forma, Ultima_Cor );
            repaint();

        }
    }
}



class Gerador_Desenho extends JPanel implements MouseListener, MouseMotionListener{

    Dimension Dimensao = Toolkit.getDefaultToolkit().getScreenSize();

    // Criando local onde ficará armazenadas as imagens.



    private BufferedImage Buffered_da_Imagem = new BufferedImage((int)Dimensao.getWidth(),
            (int)Dimensao.getHeight(), BufferedImage.TYPE_INT_RGB);

    private BufferedImage Buffered_da_Reta = new BufferedImage((int)Dimensao.getWidth(),
            (int)Dimensao.getHeight(), BufferedImage.TYPE_INT_RGB);

    private int valor;
    private Color Ultima_Cor;
    private int x;
    private int y;
    private JLabel label;

    public boolean isTipo_Algoritmo() {
        return tipo_Algoritmo;
    }

    public void setTipo_Algoritmo(boolean tipo_Algoritmo) {
        this.tipo_Algoritmo = tipo_Algoritmo;
    }

    private boolean tipo_Algoritmo = true;


    public Gerador_Desenho(){

        Graphics g_Imagem = Buffered_da_Imagem.createGraphics();
        g_Imagem.setColor(Color.WHITE);
        g_Imagem.fillRect(0, 0, Buffered_da_Imagem.getWidth(), Buffered_da_Imagem.getHeight());
        g_Imagem.dispose();

        Graphics g_Reta = Buffered_da_Reta.createGraphics();
        g_Reta.setColor(Color.WHITE);
        g_Reta.fillRect(0, 0, Buffered_da_Reta.getWidth(), Buffered_da_Reta.getHeight());
        g_Reta.dispose();

        if (Ultima_Cor == null)
            Ultima_Cor = Color.RED;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void setBuffered_da_Imagem() {
        Graphics g_Imagem = Buffered_da_Imagem.createGraphics();
        g_Imagem.setColor(Color.WHITE);
        g_Imagem.fillRect(0, 0, Buffered_da_Imagem.getWidth(), Buffered_da_Imagem.getHeight());
        g_Imagem.dispose();

        Graphics g_Reta = Buffered_da_Reta.createGraphics();
        g_Reta.setColor(Color.WHITE);
        g_Reta.fillRect(0, 0, Buffered_da_Reta.getWidth(), Buffered_da_Reta.getHeight());
        g_Reta.dispose();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, Buffered_da_Imagem.getWidth(), Buffered_da_Imagem.getHeight());
        g.drawImage(Buffered_da_Reta, 0, 0, null);

        g.dispose();
    }



    public void setForma(int newValor, Color newCor){
        valor = newValor;
        Ultima_Cor = newCor;
    }



    public void paint_retangulo(int x2, int y2){

        Graphics2D g_retangulo = Buffered_da_Reta.createGraphics();
        g_retangulo.drawImage(Buffered_da_Imagem, 0, 0, null);
        g_retangulo.setColor(Ultima_Cor);

        g_retangulo.setStroke(new BasicStroke(2.0f));

        if (x2>x && y2>y)
            g_retangulo.drawRect(x,y,x2-x,y2-y);
        if (x2>x && y>y2)
            g_retangulo.drawRect(x,y2,x2-x,y-y2);
        if (x>x2 && y>y2)
            g_retangulo.drawRect(x2,y2,x-x2,y-y2);
        if (x>x2 && y2>y)
            g_retangulo.drawRect(x2,y,x-x2,y2-y);

        g_retangulo.dispose();
    }




    public void paint_Circulo(int x2, int y2){

        Graphics2D g_Circulo = Buffered_da_Reta.createGraphics();
        g_Circulo.drawImage(Buffered_da_Imagem, 0, 0, null);
        g_Circulo.setColor(Ultima_Cor);

        g_Circulo.setStroke(new BasicStroke(2.0f));

        if (x2>x && y2>y)
            g_Circulo.drawOval(x,y,x2-x,y2-y);
        if (x2>x && y>y2)
            g_Circulo.drawOval(x,y2,x2-x,y-y2);
        if (x>x2 && y>y2)
            g_Circulo.drawOval(x2,y2,x-x2,y-y2);
        if (x>x2 && y2>y)
            g_Circulo.drawOval(x2,y,x-x2,y2-y);

        g_Circulo.dispose();
    }



    public void paint_Reta(int x2, int y2){

        Graphics2D g_Reta = Buffered_da_Reta.createGraphics();
        g_Reta.drawImage(Buffered_da_Imagem, 0, 0, null);
        // Definindo a Cor da linha
        g_Reta.setColor(Ultima_Cor);

        // Definindo a espessura da linha
       // g_Reta.setStroke(new BasicStroke(2.0f));

        // Desenhando a linha
        //g_Reta.drawLine(x, y, x2, y2);

        // delta of exact value and rounded value of the dependent variable
        if(isTipo_Algoritmo()) {
            int d = 0;

            int dx = Math.abs(x2 - x);
            int dy = Math.abs(y2 - y);
            int dx2 = 2 * dx; // slope scaling factors to
            int dy2 = 2 * dy; // avoid floating point
            int ix = x < x2 ? 1 : -1; // increment direction
            int iy = y < y2 ? 1 : -1;
            int xa = x;
            int ya = y;
            if (dx >= dy) {
                while (true) {
                    g_Reta.drawOval(xa, ya, 2, 2);
                    if (xa == x2)
                        break;
                    xa += ix;
                    d += dy2;
                    if (d > dx) {
                        ya += iy;
                        d -= dx2;
                    }
                }
            } else {
                while (true) {
                    g_Reta.drawOval(xa, ya, 2, 2);
                    if (ya == y2)
                        break;
                    ya += iy;
                    d += dx2;
                    if (d > dy) {
                        xa += ix;
                        d -= dy2;
                    }
                }

                // Atualizando a tela
                g_Reta.dispose();
            }
        }else{
            int dx = x2 - x;
            int dy = y2 - y;

            int steps = Math.abs(dx) > Math.abs(dy) ? Math.abs(dx) : Math.abs(dy);

            float Xinc = dx/ (float)steps;
            float Yinc = dy / (float)steps;

            float Xa = x;
            float Ya = y;
            for(int i = 0; i<= steps;i++){
                g_Reta.drawOval((int)Xa, (int)Ya, 2, 2);
                Xa += Xinc;
                Ya += Yinc;
            }

        }
}



    // Capturando os Eventos com o mouse
    public void mousePressed(MouseEvent e) {

        // Obtendo as coordenadas do mouse
        x = e.getX();
        y = e.getY();

        // Chamando o método Forma
        Forma(e.getX(), e.getY());


        repaint();
    }


    public void mouseReleased(MouseEvent e) {

        Forma(e.getX(), e.getY());

        Graphics g_Imagem = Buffered_da_Imagem.createGraphics();
        g_Imagem.drawImage(Buffered_da_Reta, 0, 0, null);
        g_Imagem.dispose();


        repaint();
    }


    public void mouseClicked(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        Forma(e.getX(), e.getY());


        repaint(); // Atualiza a imagem do Jpanel
    }


    public void mouseEntered(MouseEvent e) {
    }


    public void mouseExited(MouseEvent e) {
    }


    public void mouseDragged(MouseEvent e) {

        Forma(e.getX(), e.getY());
        label.setText("Posição: X: "+e.getX()+" Y:"+e.getY());
        repaint();
    }


    public void mouseMoved(MouseEvent e) {
        label.setText("Posição: X: "+e.getX()+" Y:"+e.getY());
    }

    public void getPanel(JLabel label){
        this.label = label;
    }

    public void Forma(int x, int y){

        if (valor==0)
            paint_retangulo(x, y);
        if (valor==1)
            paint_Circulo(x, y);
        if (valor==2)
            paint_Reta(x, y);

    }
}