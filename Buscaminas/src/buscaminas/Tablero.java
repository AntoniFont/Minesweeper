package buscaminas;

import ArchivoDeGuardadoIO.ArchivoDeGuardado;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.JPanel;
import utils.ArrayUtils;

public class Tablero extends JPanel {
    
    //DATOS CONSTANTES
    private static final int NUM_BOMBAS = 10;
    private static final int NUM_FILAS = 9;
    private static final int NUM_COLUMNAS = 9;
    //Constante que indica cuantas casillas hay que no son bomba
    private static final int NUM_CASILLAS_SIN_BOMBA = NUM_FILAS*NUM_COLUMNAS - NUM_BOMBAS;
    //VARIABLES
    private static int posicionBombas[] = new int[NUM_BOMBAS];
    private static Casilla casillas[][] = new Casilla[NUM_FILAS][NUM_COLUMNAS];
    //Variable que indica el numero de casillas destapadas en la partida actual
    private static int casillasDestapadas = 0;
    
    public Tablero(){
        //Generamos las posiciones de las bombas (de 0 a 80)
        posicionBombas = generarPosicionBombas();
        
        //Iteramos por todas las posiciones y generamos una casilla con bomba o sin bomba segun corresponde
        for(int posActual = 0; posActual < NUM_FILAS * NUM_COLUMNAS; posActual++) {
            int filaActual = convertirPosicionAFila(posActual);
            int columnaActual = convertirPosicionAColumna(posActual);
            if (seHaGeneradoBombaEnLaPos(posActual)) {
                //Generamos casilla sin bomba
                casillas[filaActual][columnaActual] = new Casilla(filaActual,columnaActual,true);
            } else {
                //Generamos casilla sin bomba
                casillas[filaActual][columnaActual] = new Casilla(filaActual,columnaActual,false);
            }
        }
        
        //Se cuentan las bombas de alrededor y añadimos ese numero a la casilla
        for(int i = 0; i<NUM_FILAS;i++){
            for(int j = 0;j<NUM_COLUMNAS;j++){
                if(!casillas[i][j].isBomba()){
                    int numeroBombas = contarBombasAlrededor(casillas[i][j]);
                    casillas[i][j].setNumBombasAlrededor(numeroBombas);
                }
            }
        }
        initComponents() ;
    }

    private void setIconosPorDefecto(){
        for(int i = 0;i<NUM_FILAS;i++){
            for(int j= 0;j<NUM_COLUMNAS;j++){
                casillas[i][j].setImagenPorDefecto();
            }
        }
    }
   
    private void initComponents(){
        GridLayout layout = new GridLayout(NUM_FILAS,NUM_COLUMNAS);
        layout.setHgap(3);
        layout.setVgap(3);
        this.setLayout(layout);
        for(int i =0;i<NUM_FILAS;i++){
            for(int j= 0;j<NUM_COLUMNAS;j++){
                this.add(casillas[i][j]);
            }
        } 
        setIconosPorDefecto();
    }
    
    private int contarBombasAlrededor(Casilla casilla){
        Casilla casillasAlrededor[] = getCasillasAlrededor(casilla);
        int numBombasAlrededor = 0;
        for (Casilla c : casillasAlrededor) { 
           //Si existe la casilla hay casillas,como las de los lados, que no tienen 8 casillas adyacentes)
           //Y tiene bomba
           if(c != null && c.isBomba()){ 
               numBombasAlrededor++;
           }		
        }
        return numBombasAlrededor;
    }
    
    private Casilla[] getCasillasAlrededor(Casilla casilla){
        Casilla casillasAlrededor[] = new Casilla[8];
        int filaCasillaSeleccionada = casilla.getFila();
        int columnaCasillaSeleccionada = casilla.getColumna();
        //  c c c
        //  c x c   x: casilla seleccionada
        //  c c c 
        //posiciones a sumar para obtener las casillas adyacentes
        int [][] posiciones = {     
            {-1,-1},    //casilla al nor-oeste
            {-1,0},     //casilla al oeste
            {-1,1},     //casilla sur-oeste
            {0,-1},     //casilla al norte
            {0,1},      //casilla al sur
            {1,-1},     //casilla al nor-este
            {1,0},      //casilla al este
            {1,1}       //casilla al sur-este
        };          
        for (int i = 0; i < posiciones.length; i++) {
            int posFilaCasillaAdyacente = casilla.getFila() + posiciones[i][0];
            int posColCasillaAdyacente = casilla.getColumna() + posiciones[i][1];
            //obtenemos la posicion de la casilla adyacente la cual vamos a comprobar
            //si es una posicion valida del tablero
 
            if (existeEstaPosEnElTablero(posFilaCasillaAdyacente,posColCasillaAdyacente)){
                casillasAlrededor[i] = casillas[posFilaCasillaAdyacente][posColCasillaAdyacente];
            }else{
                casillasAlrededor[i] = null;
            }
        }
        return casillasAlrededor;
    }
    
    //Método que devuelve si una casilla(mediante sus coordenadas fila-columna)
    //puede existir o si en cambio estaría fuera de las medidas del tablero
    private boolean existeEstaPosEnElTablero(int posFila, int posCol){
        return (posFila >= 0 && posFila < NUM_FILAS) &&
                (posCol >= 0 && posCol < NUM_COLUMNAS);
    }
    
    //A partir de una posicion (de 0 a 80) devuelve la fila en la que se encuentra
    public int convertirPosicionAFila(int posicion) {
        return (int) (posicion / NUM_FILAS);
    }

    //A partir de una posicion (de 0 a 80) devuelve la columna en la que se encuentra
    public int convertirPosicionAColumna(int posicion) {
        return (posicion % NUM_COLUMNAS);
    }
 
    //Genera un array de 10 numeros no repetidos que van desde 0 hasta (NUM_FILAS*NUM_COLUMNAS-1)
    private int[] generarPosicionBombas() {
        //Hardcodeo cosas para hacer pruebas
        int posBomb[] = new int[NUM_BOMBAS];

        Random r = new Random();
        for(int i = 0; i < posBomb.length; i++){
            boolean numRepetido;
            int n;
            //comprovam si el numero general aleatori esta repetit, si es aixi un numero nou
            //fins que el numero que generi no estigui repetit, llavors el posam a l'array posRandom
            do {
                numRepetido = false;
                n = r.nextInt(9*9-1); //genera numero random entre 0 y el total de casillas
                if(ArrayUtils.arrayContieneEntero(posBomb,n)) {
                        numRepetido = true;
                }
            }while(numRepetido);
            posBomb[i] = n;
        }
        return posBomb;
    }

    //TODO cambiar este esquema a una búsqueda con while
    //Método que comprueba si se ha generado una bomba en una posición determinada
    //del tablero (de 0 a 80)
    private boolean seHaGeneradoBombaEnLaPos(int posicion) {
        boolean posicionEncontrada = false;
        int j = 0;
        
        //Busqueda si la posicion de la casilla pasada como parametro, esta
        //dentro del array de posiciones con bomba
        while(j < posicionBombas.length && !posicionEncontrada){
            if (posicion == posicionBombas[j]) {
                posicionEncontrada = true;
            }
            j++;
        }
        
        return posicionEncontrada;
    }
        
    //Método que al clickar una casilla, evalua si se ha ganado, perdido, o si
    //por lo contrario, el juego sigue
    public static void evaluarCondicionVictoria(int fila , int columna){
        
        //Si se ha destapado una casilla con bomba, finaliza la partida con derrota
        if(casillas[fila][columna].isBomba()){
            
            //el parametro false indica que se finaliza el juego con derrota
            Buscaminas.finalizarPartidaDerrota();
            
            //Si se han destapado todas las casillas sin bomba, finaliza la partida
            //con victoria
        }else if(casillasDestapadas == NUM_CASILLAS_SIN_BOMBA){
            
            //el parametro true indica que se finaliza el juego con victoria
            Buscaminas.finalizarPartidaVictoria();
        }
        
        
    }
    
    public void destaparTodasLasCasillas(){
        for(int i = 0;i<NUM_FILAS;i++){
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                if(casillas[i][j].isTapado()){
                    casillas[i][j].destapar();
                }
            }
        }
    }
    
    public void taparTodasLasCasillas(){
        for(int i = 0;i<NUM_FILAS;i++){
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                if(!casillas[i][j].isTapado()){
                    casillas[i][j].tapar();
                }
            }
        }
        casillasDestapadas = 0;
    }
    
    public Casilla[][] getCasillas(){
        return casillas;
    }
    
    public void setCasillas(Casilla [][] casillas){
        this.casillas = casillas;
    }
    
    //Método que incrementa el numero
    public static void incrementarCasillasDestapadas(){
        if(casillasDestapadas < NUM_CASILLAS_SIN_BOMBA) casillasDestapadas++;
    }
    
    public void setNumCasillasDestapadas(int num){
        casillasDestapadas = num;
    }
    
    public void reconstruirPartidaDesdeArchivoGuardado(ArchivoDeGuardado archivoGuardado){
        for(int i = 0; i< NUM_FILAS; i++){
                for(int j = 0; j < NUM_COLUMNAS; j++){
                boolean tieneBomba = archivoGuardado.getCasillaIsBomba(i,j);
                boolean isTapada = archivoGuardado.getCasillaIsTapada(i, j);
                int numBombasAlrededor = archivoGuardado.getNumBombasAlrededor(i,j);
                int filaCasilla = archivoGuardado.getFilaCasilla(i,j);
                int columnaCasilla = archivoGuardado.getColumnaCasilla(i,j);
                
                casillas[i][j].setTieneBomba(tieneBomba);
                casillas[i][j].setTapado(isTapada);
                casillas[i][j].setNumBombasAlrededor(numBombasAlrededor);
                casillas[i][j].setFila(filaCasilla);
                casillas[i][j].setColumna(columnaCasilla);
                
                //Actualizar iconos
                if(casillas[i][j].isTapado()){
                    //la imagen por defecto es la que se muestra cuando una casilla
                    //esta tapada
                    casillas[i][j].setImagenPorDefecto();
                    
                }else if(!casillas[i][j].isTapado()){
                    casillas[i][j].destapar();
                }               
            }
        }
    }
}
