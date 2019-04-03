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

class JanelaComandos extends JFrame{
    // Barra de Ferramenta com os botões necessários
    private JToolBar Barra_De_Ferramenta = new JToolBar();
    private JButton Retangulo = new JButton("Retângulo");
    private JButton Triangulo = new JButton("Triângulo");
    private JButton Reta = new JButton("Reta");
    private JButton Editar_Cor = new JButton("Editar Cor");
    private JButton LimpaTela = new JButton("Limpa Tela");
    private JLabel xlb = new JLabel("X:");
    private JLabel ylb = new JLabel("Y:");
    private JTextField x_txtf = new JTextField();
    private JTextField y_txtf = new JTextField();
    
    private JPanel panel = new JPanel(new BorderLayout());

    private Gerador_Desenho Panel_Desenho = new Gerador_Desenho();

    private Color Cor_da_Figura = Color.RED;
    private Color Ultima_Cor = Color.RED;

    private JLabel Status = new JLabel("Forma ativa: Retângulo");
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
        Barra_De_Ferramenta.add(Triangulo);
        Barra_De_Ferramenta.add(Reta);
        Barra_De_Ferramenta.add(Editar_Cor);
        Barra_De_Ferramenta.add(LimpaTela);
        Barra_De_Ferramenta.add(Bresenham);
        Barra_De_Ferramenta.add(DDA);
        Barra_De_Ferramenta.add(xlb);
        Barra_De_Ferramenta.add(x_txtf);
        Barra_De_Ferramenta.add(ylb);
        Barra_De_Ferramenta.add(y_txtf);

        // Cor de fundo da Barra de Status
        Status.setBackground(Color.WHITE);

        add(Barra_De_Ferramenta, BorderLayout.NORTH);
        add(Panel_Desenho, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
       // add(Posicao, BorderLayout.SOUTH);

        Eventos_Desenhando Eventos = new Eventos_Desenhando();
        Retangulo.addActionListener(Eventos);
        Triangulo.addActionListener(Eventos);
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

            if (event.getSource() == Triangulo){
                Forma=1;
                Status.setText("  Forma ativa: Triângulo");
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
 class UltimosPontos{
    int x;
    int y;
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
    private int x_txtf;
    private int y_txtf;


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

    public void paint_teste(int x2,int y2){
        Graphics2D g_retangulo = Buffered_da_Reta.createGraphics();
        g_retangulo.drawImage(Buffered_da_Imagem, 0, 0, null);
        g_retangulo.setColor(Ultima_Cor);
        g_retangulo.setStroke(new BasicStroke(2.0f));
    }
    
    public void paint_retangulo(int x2, int y2){
        Graphics2D g_retangulo = Buffered_da_Reta.createGraphics();
        g_retangulo.drawImage(Buffered_da_Imagem, 0, 0, null);
        g_retangulo.setColor(Ultima_Cor);
        g_retangulo.setStroke(new BasicStroke(2.0f));

        ultimaLinhaDoTriangulo(x,y,x,y2,g_retangulo);
        ultimaLinhaDoTriangulo(x,y2,x2,y2,g_retangulo);
        ultimaLinhaDoTriangulo(x2,y2,x2,y,g_retangulo);
        ultimaLinhaDoTriangulo(x2,y,x,y,g_retangulo);

//        if (x2>x && y2>y)
//            g_retangulo.drawRect(x,y,x2-x,y2-y);
//        if (x2>x && y>y2)
//            g_retangulo.drawRect(x,y2,x2-x,y-y2);
//        if (x>x2 && y>y2)
//            g_retangulo.drawRect(x2,y2,x-x2,y-y2);
//        if (x>x2 && y2>y)
//            g_retangulo.drawRect(x2,y,x-x2,y2-y);

//        int a, xi, yi;
//        int dx,dy, dx2, dy2, dxdy2, p, end;
//
//        if(isTipo_Algoritmo()) {
//            //Bresenham
//            for(a=1;a<5;a++){
//                if(a==1){ //horizontal superior
//                    x=100;
//                    y=100;
//                    x2=400;
//                    y2=100;
//                }
//                if(a==2){ //vertical esquerda
//                    x=100;
//                    y=100;
//                    x2=100;
//                    y2=300;
//                }
//                if(a==3){ //horinzontal inferior
//                    x=100;
//                    y=300;
//                    x2=400;
//                    y2=300;
//                }
//                if(a==4){ //vertical direita
//                    x=400;
//                    y=100;
//                    x2=400;
//                    y2=300;
//                }
//                if(x==x2){
//                    xi=x;
//                    yi=y;
//                    while(yi!=y2){
//                        if((y2-y)>0)
//                            ++yi;
//                        else
//                            --yi;
//                            g_retangulo.drawOval(xi,yi,2,2); //rasters verticais esquerda e direita
//                        }
//                        if(y>=y2){
//                            xi=x2;
//                            yi=y2;
//                            end=y2;
//                        }
//                    }
//                    if(x<x2){
//                        xi=x;
//                        yi=y;
//                        end=x2;
//                    }else{
//                        xi=x2;
//                        yi=y2;
//                        end=x;
//                    }
//
//                dx=Math.abs(x2-x);
//                dy=Math.abs(y2-y);
//                dy2=2*dy;
//                dxdy2=2*(dy-dx);
//                p=2*dy-dx;
//                while(xi<end){
//                    g_retangulo.drawOval(xi,yi,2,2); // rasters horizontais inferior e superior
//                    if(p<0){
//                        xi++;
//                        p+=dy2;
//                    }else{
//                        xi++;
//                        yi++;
//                        p=dxdy2;
//                    }
//                }
//            }
//        }else{
//            //dda
//            for(a=1;a<5;a++){
//                if(a==1){ //horizontal superior
//                    x=150; //100
//                    y=150; //100
//                    x2=450; //400
//                    y2=150; //100
//                }
//                if(a==2){ //vertical esquerda
//                    x=150; //100
//                    y=150; //100
//                    x2=150; //100
//                    y2=350; //300
//                }
//                if(a==3){ //horinzontal inferior
//                    x=150; //100
//                    y=350; //300
//                    x2=450; //400
//                    y2=350; //300
//                }
//                if(a==4){ //vertical direita
//                    x=450; //400
//                    y=150; //100
//                    x2=450; //400
//                    y2=350; //300
//                }
//                dx = Math.abs(x2 - x);
//                dy = Math.abs(y2 - y);
//                int length;
//                if (Math.abs(dx) >= Math.abs(dy)){
//                    length=Math.abs(dx);
//                }else{
//                    length=Math.abs(dy);
//                }
//                float Xinc = dx / (float)length;
//                float Yinc = dy / (float)length;
//
//                float Xa = x;
//                float Ya = y;
//                int i = 1;
//                while(i<=length){
//                    g_retangulo.drawOval((int)Xa, (int)Ya,2,2);
//                    Xa += Xinc;
        
//                    Ya += Yinc;
//                    i++;
//                }
//            }
//        }
        g_retangulo.dispose();
    }

    public void paint_Triangulo(int x2, int y2){

        Graphics2D g_Triangulo = Buffered_da_Reta.createGraphics();
        g_Triangulo.drawImage(Buffered_da_Imagem, 0, 0, null);
        g_Triangulo.setColor(Ultima_Cor);

        g_Triangulo.setStroke(new BasicStroke(2.0f));
        UltimosPontos up1 = new UltimosPontos();
        UltimosPontos up2 = new UltimosPontos();

        up1 = Algoritmos(x2-30,y2+30,g_Triangulo);
        up2 = Algoritmos(x2+30,y2+30,g_Triangulo); 
       // g_Triangulo.drawLine(up1.x,up1.y,up2.x,up2.y);
        ultimaLinhaDoTriangulo(up1.x,up1.y,up2.x,up2.y,g_Triangulo);
        //Algoritmos(x2+60,y2+60,g_Triangulo);

//        if (x2>x && y2>y)
//            g_Triangulo.drawRect(x,y,x2-x,y2-y);
//        if (x2>x && y>y2)
//            g_Triangulo.drawRect(x,y2,x2-x,y-y2);
//        if (x>x2 && y>y2)
//            g_Triangulo.drawRect(x2,y2,x-x2,y-y2);
//        if (x>x2 && y2>y)
//            g_Triangulo.drawRect(x2,y,x-x2,y2-y);

        g_Triangulo.dispose();
    }
    public void ultimaLinhaDoTriangulo(int a,int b,int x2,int y2,Graphics2D g_Reta){
        if(isTipo_Algoritmo()) {
            int d = 0;
            int dx = Math.abs(x2 - a);
            int dy = Math.abs(y2 - b);
            int dx2 = 2 * dx; // slope scaling factors to
            int dy2 = 2 * dy; // avoid floating point
            int ix = a < x2 ? 1 : -1; // increment direction
            int iy = b < y2 ? 1 : -1;
            int xa = a;
            int ya = b;
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
            }
        }else{
            int dx = x2 - a;
            int dy = y2 - b;

            int steps = Math.abs(dx) > Math.abs(dy) ? Math.abs(dx) : Math.abs(dy);

            float Xinc = dx/ (float)steps;
            float Yinc = dy / (float)steps;

            float Xa = a;
            float Ya = b;
            for(int i = 0; i<= steps;i++){
                g_Reta.drawOval((int)Xa, (int)Ya, 2, 2);
                Xa += Xinc;
                Ya += Yinc;
            }
        }
    }

    public UltimosPontos Algoritmos(int x2,int y2,Graphics2D g_Reta){
        UltimosPontos up = new UltimosPontos();
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
            }

            up.x = xa;
            up.y = ya;
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
            up.x = (int)Xa;
            up.y = (int)Ya;
        }
        return up;
    }
    public void paint_Reta(int x2, int y2){

        Graphics2D g_Reta = Buffered_da_Reta.createGraphics();
        g_Reta.drawImage(Buffered_da_Imagem, 0, 0, null);
        // Definindo a Cor da linha
        g_Reta.setColor(Ultima_Cor);
        Algoritmos(x2,y2,g_Reta);
        // Definindo a espessura da linha
       // g_Reta.setStroke(new BasicStroke(2.0f));

        // Desenhando a linha
        //g_Reta.drawLine(x, y, x2, y2);

        // delta of exact value and rounded value of the dependent variable
    }

    // Capturando os Eventos com o mouse
    public void mousePressed(MouseEvent e) {
        // Obtendo as coordenadas do mouse e TxtField
        x = e.getX();
        y = e.getY();
        
        x_txtf = e.getX();
        y_txtf = e.getY();
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
        // Obtendo as coordenadas do mouse e TxtField
        x = e.getX();
        y = e.getY();
        
        x_txtf = e.getX();
        y_txtf = e.getY();

        Forma(e.getX(), e.getY());
        repaint(); // Atualiza a imagem do Jpanel
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

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
            paint_Triangulo(x, y);
        if (valor==2)
            paint_Reta(x, y);
    }
}