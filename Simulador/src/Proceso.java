/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.JLabel;

/**
 *
 * @author HP15DA0023LA
 */
public class Proceso extends JLabel{
    private Integer tamaño, tiempo, min, max,processID;
    private String hexadecimal;

    public Proceso(Integer min) {
        // los procesos van de 1 ms a 20 ms
        this.tiempo = (int)(Math.random()*20.0+1.0);
        this.tamaño = tiempo*50; // el tamaño va a ser en MB y el maximo de la memoria principal es de 4 GB
        this.hexadecimal= Integer.toHexString(this.tamaño);
        this.min=min;
        this.max=min+tamaño;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public String getHexadecimal() {
        return hexadecimal;
    }
    public boolean reducir(){
        boolean seAcabo=true;
        tiempo-=3;
        if(tiempo>0) seAcabo=false;
        return seAcabo;
    }

    public Integer getTiempo() {
        return tiempo;
    }

    public Integer getProcessID() {
        return processID;
    }

    public void setProcessID(Integer processID) {
        this.processID = processID;
    }
    
    public Integer getTamaño() {
        return tamaño;
    }
    public String getMinComoHexa() {
        return Integer.toHexString(this.min);
    }
    public String getMaxComoHexa() {
        return Integer.toHexString(this.max);
    }
    public Integer getTamañoEnPix() {
        return (tamaño*250/4096);
    }
}
