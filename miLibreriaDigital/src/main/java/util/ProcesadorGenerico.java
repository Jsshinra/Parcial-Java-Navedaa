package util;


public class ProcesadorGenerico<T> {

    private T elemento;

    public ProcesadorGenerico(T elementoInicial) {
        this.elemento = elementoInicial;
    }


    public void procesarElemento() {
        if (this.elemento != null) {
            System.out.println("Procesando elemento: " + elemento.toString());
            System.out.println("Tipo del elemento: " + elemento.getClass().getSimpleName());
        } else {
            System.out.println("No hay elemento para procesar (es nulo).");
        }
    }
 
    
}
