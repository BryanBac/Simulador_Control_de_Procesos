
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gonzc
 */
public class Simulador extends javax.swing.JFrame {
     ImagenFondo fondo = new ImagenFondo();
     ArrayList<Proceso> listaProcesos = new ArrayList();
     ArrayList<JLabel> listaLabel = new ArrayList();
     ArrayList<JLabel> listaLabelLimite = new ArrayList();
     ArrayList<JLabel> listaLabelBase = new ArrayList();
     Border border = BorderFactory.createLineBorder(Color.ORANGE);
     Border borderM = BorderFactory.createLineBorder(Color.WHITE);
     Border borderI = BorderFactory.createLineBorder(Color.PINK);
     int nuevaPosición=350; //este es el punto inicial de altura donde spamean los cuadraditos
     Integer processID=0;
     Integer processIDIndividual=0;
     JLabel memoriaPrincipal = new JLabel("");
     // variable para eliminar
     int contElim = 0;
     int tiempoEjecucion;
     int quantum = 3;
     int inicio;
     int instruccion;
     JLabel aux;
     /*****************************Variables Diego******************************/
     
     int alto = 100;
     int bajo = 350;
     int NP = 350;
     int min = 350;
     //Agragar etiqueta de instruccion
     JLabel v = new JLabel("");
     JLabel recorrido = new JLabel("");
     int ejey;
     int variacion=0;
    /**
     * Creates new form Simulador
     */
    public Simulador() {
       this.setContentPane(fondo);
       initComponents();
       Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
       int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
       int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
       this.setLocation(x, y);
       this.setTitle("Simulador control de procesos");
       //icono del programa
       Image ico = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("Imagenes/2.jpg"));
       this.setIconImage(ico);
       //Hilo hora
       HiloHora hilohora = new HiloHora();
       hilohora.startRunning();
       hilohora.start();
       //hilo eliminar
       HiloEliminar elimiHilo = new HiloEliminar();
       elimiHilo.start();
       memoriaPrincipal.setBounds(200,100, 80, 250);
       this.jPanel1.add(memoriaPrincipal);
       memoriaPrincipal.setBorder(borderM);
    }
    public String ObtenerHora(){
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
    String hora =  formato.format(LocalDateTime.now());
    return hora;
}
    public class HiloHora extends Thread{
        private boolean run = false;
        public void startRunning(){
           run = true;
        }
        public void stopRunning(){
            run = false;
        }
        @Override
        public void run(){
            while(run){
                //Actualizar hora
                HoraSistema.setText(ObtenerHora()+ " hrs");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Simulador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Simulador.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
 }
    public class HiloEliminar extends Thread {
        @Override
        public void run() {
            while (true) {
                jLabel1.setText(jLabel1.getText());     //No reconoce la siguiente condición sin esto? Si no les da problema se puede borrar
                if (!listaProcesos.isEmpty() && !listaLabel.isEmpty()) {
                    Activador();
                    /*Información del proceso actual*/
                    tiempoEjecucion = listaProcesos.get(contElim).getTiempo();  //Recibe el tiempo que le queda al proceso
                    aux = listaLabel.get(contElim);                             //Obtiene el JLabel del proceso, de la lista de JLabels
                    aux.setBorder(javax.swing.BorderFactory.createLineBorder(Color.red, 4));
                    jTextPane2.setText("Proceso "+listaProcesos.get(contElim).getProcessID());
                    inicio = listaProcesos.get(contElim).getPosicion();         //Obtiene la posición del inicio del proceso
                    jTextPane7.setText(Integer.toHexString(inicio));
                    inicio = inicio - ((listaProcesos.get(contElim).getTamaño()/50)*3);  //Obtiene la posición del fin del proceso   
                    jTextPane8.setText(Integer.toHexString(inicio));
                    //El tiempo de ejecución restante es menor o igual al quantum
                    if (listaProcesos.get(contElim).reducir()) {
                        while (tiempoEjecucion > 0) {         
                            instruccion = inicio+(tiempoEjecucion*3);           //Calcula la posición de la siguiente instrucción
                            jTextPane6.setText(Integer.toHexString(instruccion));
                            tiempoEjecucion--;
                            Recorrer();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Simulador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        EliminarEtiquetas();
                        Eliminar();
                        v.setVisible(false);
                        recorrido.setVisible(false);
                    }//El tiempo de ejecución restante es mayor al quantum
                    else{
                        for (int i = 0; i < quantum; i++) {
                            instruccion = inicio+(tiempoEjecucion*3);
                            jTextPane6.setText(Integer.toHexString(instruccion));
                            Recorrer();
                            tiempoEjecucion--;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Simulador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        contElim++; //Pasa al siguiente proceso
                        aux.setBorder(border);
                    }
                    jTextPane2.setText("");
                    jTextPane6.setText("");
                    jTextPane7.setText("");
                    jTextPane8.setText("");
                    if (contElim >= listaProcesos.size()) {
                        contElim = 0;
                    }
                }
            }
        }
    }
    public void Activador(){
        if(listaProcesos.size()!=1){
            /*ACTIVADOR*/
            jLabel9.setFont(new java.awt.Font("Calibri Light", 1, 14)); //Cambia la fuente
            jLabel9.setForeground(Color.red);
            jTextPane2.setText("Activador");    //Cambio en la etiqueta de calendarizador
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulador.class.getName()).log(Level.SEVERE, null, ex);
            }
            jLabel9.setFont(new java.awt.Font("Calibri Light", 0, 14)); //Regresa la etiqueta a su estado original
            jLabel9.setForeground(Color.black);
        }
    }
  public int VerificarCreacion(int tp) {
        int tproceso = tp;
        int espacio;
        int espacio2;
        int aux;
        //Verificamos si la lista está vacía, si está creamos el proceso
        if (listaProcesos.size() == 0) {
            alto = 100;
            bajo = 350;
            NP = 350;
            min = 350;
            NP = NP - tproceso;
            //SI PERMITE
            return 1;
        } //Si no está vacia
        else {
            min = listaProcesos.get(0).getMin();
            //Verificar si NP es menor que minimo
            if (NP < min) {
                //Verificar que el espacio disponible entre alto y NP sea suficiente para el proceso
                espacio = NP - alto;
                if (espacio >= tproceso) {
                    //Actualizar NP
                    NP = NP - tproceso;
                    //SI PERMITE
                    return 1;
                } else { //Si el espacio entre alto y NP no es suficiente se debe verificar en la parte de bajo
                    aux = NP;
                    //Actualizamos NP y lo pasamos a bajo
                    NP = bajo;
                    //Si NP es igual a minimo significa que no hay espacio
                    if (NP == min) {
                        //NO PERMITE
                        return 0;
                    } //Sino verifica que min sea menor a NP
                    else if (min < NP) {
                        //Verificar que el espacio disponible entre NP y min sea suficiente para el proceso
                        espacio2 = NP - min;
                        if (espacio2 > tproceso) {
                            NP = NP - tproceso;
                            //SI PERMITE
                            return 1;
                        } else {
                            //NP regresa a su posicion anterior
                            NP = aux;
                            //NO PERMITE
                            return 0;
                        }
                    }
                }
            } //Significa que NP es mayor a min
            else {
                //Verificar que el espacio disponible entre NP y min sea suficiente para el proceso
                espacio2 = NP - min;
                if (espacio2 > tproceso) {
                    NP = NP - tproceso;
                    //SI PERMITE
                    return 1;
                } else {
                    //NO PERMITE
                    return 0;
                }
            }

        }
        return 0;
    }
    // falta mucho que componer acá, en especial con los indices
    public void Eliminar() {
        historial.setText(historial.getText() + "P" + listaProcesos.get(contElim).getProcessID() + " finalizado a las " + ObtenerHora() + " hrs\n");
        JLabel x = listaLabel.get(contElim);//Obtiene el JLabel del proceso, de la lista de JLabels
        x.setBounds(200, listaProcesos.get(contElim).getPosicion(), 80, listaProcesos.get(contElim).getTamañoEnPix());
        jPanel1.remove(x);//Finalmente se elimina de la memoria principal el proceso
        listaLabel.remove(contElim);
        listaProcesos.remove(contElim);
        if(listaProcesos.size()>0) min = listaProcesos.get(0).getMin();
        //System.out.println(NP + " " + min + " " + listaProcesos.get(0).getProcessID());
        processID--;
    }
     public void EliminarEtiquetas(){
       listaLabelBase.get(contElim).setVisible(false);
       listaLabelBase.remove(contElim);
       listaLabelLimite.get(contElim).setVisible(false);
       listaLabelLimite.remove(contElim);//Finalmente se eliminan
    }
    public void Error(){
        UIManager.put("control", new Color(87,20,100));
        UIManager.put("nimbusBase", new Color(87, 20, 100));
        String ms1= "<html><p style = \"color:white; font:15px; \">  Error</p></html>";
        JOptionPane.showMessageDialog(null, ms1, " ",JOptionPane.ERROR_MESSAGE);
    }
    public void Recorrer(){
        //Etiqueta de instruccion
        //Actualizar en la memoria principal la label del de instrucción en ejecucion
        if(!listaProcesos.get(contElim).getCalculado()){
        int lim1 = listaProcesos.get(contElim).getPosicion()-listaProcesos.get(contElim).getTamañoEnPix();//Limite
        int lim2 = listaProcesos.get(contElim).getPosicion()-21;//Base
        int tamañoReal = lim2-lim1;
        int intervalo = tamañoReal/listaProcesos.get(contElim).getTiempo();
        listaProcesos.get(contElim).setCalculado(true);
        listaProcesos.get(contElim).setVariacion(intervalo);
        listaProcesos.get(contElim).setAltura(listaProcesos.get(contElim).getPosicion()-8);
        }
        variacion = listaProcesos.get(contElim).getAltura()-listaProcesos.get(contElim).getVariacion();//Moviliza la barra y etiqueta conforme
        listaProcesos.get(contElim).setAltura(variacion);                                               //se ejecuten las instrucciones
        v.setBounds(280, listaProcesos.get(contElim).getAltura() , 30, 20);
        v.setText(Integer.toHexString(instruccion));
        v.setVisible(true);
        jPanel1.add(v);
        //barra de recorrido
        recorrido.setBounds(200, listaProcesos.get(contElim).getAltura(), 80, 10);
        recorrido.setVisible(true);
        ImageIcon imagen = new ImageIcon("src/Imagenes/1.jpg");
        Icon icono = new ImageIcon(imagen.getImage().getScaledInstance(90, 90, Image.SCALE_DEFAULT));
        recorrido.setBackground(Color.CYAN);
        recorrido.setIcon(icono);
        jPanel1.add(recorrido);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jPanel1 = new ImagenRobot();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        panelProcesador = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextPane6 = new javax.swing.JTextPane();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextPane7 = new javax.swing.JTextPane();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextPane8 = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        historial = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        HoraSistema = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(102, 0, 102));
        jButton1.setText("Agregar un nuevo proceso");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jLabel9.setText("    Activador");
        jLabel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jLabel8.setFont(new java.awt.Font("Calibri Light", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Memoria principal");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Calibri Light", 0, 18)); // NOI18N
        jLabel10.setText("       S.O.");
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jLabel11.setText("0x0000h");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jLabel12.setText("0x00FFh");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(166, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(34, 34, 34))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(50, 50, 50)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(248, 248, 248)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        panelProcesador.setBackground(new java.awt.Color(153, 153, 255));

        jLabel3.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jLabel3.setText("Calendarizador");

        jScrollPane2.setViewportView(jTextPane2);

        jLabel5.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jLabel5.setText("Contador de programa");

        jLabel4.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jLabel4.setText("Base");

        jLabel6.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jLabel6.setText("Límite");

        jLabel7.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jLabel7.setText("Historial");

        jScrollPane6.setViewportView(jTextPane6);

        jScrollPane7.setViewportView(jTextPane7);

        jScrollPane8.setViewportView(jTextPane8);

        historial.setColumns(20);
        historial.setRows(5);
        jScrollPane3.setViewportView(historial);

        jLabel1.setFont(new java.awt.Font("Calibri Light", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Hora del sistema");

        HoraSistema.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        HoraSistema.setForeground(new java.awt.Color(255, 255, 255));
        HoraSistema.setText("   ");

        jLabel2.setFont(new java.awt.Font("Calibri Light", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Procesador");

        javax.swing.GroupLayout panelProcesadorLayout = new javax.swing.GroupLayout(panelProcesador);
        panelProcesador.setLayout(panelProcesadorLayout);
        panelProcesadorLayout.setHorizontalGroup(
            panelProcesadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProcesadorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
            .addGroup(panelProcesadorLayout.createSequentialGroup()
                .addGroup(panelProcesadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProcesadorLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7))
                    .addGroup(panelProcesadorLayout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addGroup(panelProcesadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelProcesadorLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(HoraSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelProcesadorLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(59, 59, 59))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelProcesadorLayout.createSequentialGroup()
                .addGroup(panelProcesadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelProcesadorLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelProcesadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelProcesadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))))
                    .addGroup(panelProcesadorLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelProcesadorLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(80, 80, 80))
        );
        panelProcesadorLayout.setVerticalGroup(
            panelProcesadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProcesadorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelProcesadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelProcesadorLayout.createSequentialGroup()
                        .addGroup(panelProcesadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(8, 8, 8)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(HoraSistema)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addGap(10, 10, 10)
                .addComponent(panelProcesador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(253, 253, 253))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelProcesador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Proceso pr= new Proceso(NP); // aquí debería ir el actual que vas a agregar Diego, en lugar de cero.
        pr.setProcessID(processIDIndividual);
        // y si toca eliminar uno solo buscamos en la lista cual processID es igual y lo sacamos,
        //Actualizar el contador
        // luego cambiamso los demás process Id de la lista (los que van despues del que sacamos) y  ya
        
        if (VerificarCreacion(pr.getTamañoEnPix()) == 1) {
            this.listaProcesos.add(pr);
            JLabel y = new JLabel("ProcessID: " + processIDIndividual.toString());
            //nuevaPosición=nuevaPosición-listaProcesos.get(processID).getTamañoEnPix();
            y.setBounds(200, NP, 80, listaProcesos.get(processID).getTamañoEnPix());
            this.jPanel1.add(y);
            y.setBorder(border);
            listaLabel.add(y);
            listaProcesos.get(processID).setPosicion(NP + listaProcesos.get(processID).getTamañoEnPix());
            //Registra en el historial
            this.historial.setText(this.historial.getText() + "P" + processIDIndividual + " creado a las " + this.ObtenerHora() + " hrs\n");
            processIDIndividual++;
            //Etiquetas
            //Agregar étiqueta limite
            inicio = listaProcesos.get(processID).getPosicion();         //Obtiene la posición del inicio del proceso 
            JLabel z = new JLabel(""+ Integer.toHexString(inicio));
            z.setBounds(170,listaProcesos.get(processID).getPosicion()-listaProcesos.get(processID).getTamañoEnPix(), 30, 20);
            jPanel1.add(z);
            z.setBorder(borderI);
            listaLabelLimite.add(z);
            //Agregar la etiquetabase
            inicio = inicio - ((listaProcesos.get(processID).getTamaño()/50)*3);  //Obtiene la posición del fin del proceso   
            JLabel w = new JLabel(""+ Integer.toHexString(inicio));
            w.setBounds(170,listaProcesos.get(processID).getPosicion()-21, 30, 20);
            jPanel1.add(w);
            w.setBorder(borderI);
            listaLabelBase.add(w);
            processID++;
        }
        else{
            this.Error();
        }
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Simulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Simulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Simulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Simulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Simulador().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel HoraSistema;
    private javax.swing.JTextArea historial;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JTextPane jTextPane6;
    private javax.swing.JTextPane jTextPane7;
    private javax.swing.JTextPane jTextPane8;
    private javax.swing.JPanel panelProcesador;
    // End of variables declaration//GEN-END:variables
class ImagenFondo extends JPanel{
        private Image imagen;
        @Override
        public void paint(Graphics g){
            imagen = new ImageIcon(getClass().getResource("/Imagenes/4.jpg")).getImage();
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            setOpaque(false);
            super.paint(g);
        }
    }
class ImagenRobot extends JPanel{
        private Image imagen;
        @Override
        public void paint(Graphics g){
            imagen = new ImageIcon(getClass().getResource("/Imagenes/3.png")).getImage();
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            setOpaque(false);
            super.paint(g);
        }
    }
}
// cuando se vaya a eliminar un proceso solo crear una label identica al proceso pero sin texto y con bordes blancos