package com.afundacion.fp.clips;

public class Server {
    /**
     * Esta variable necesita ser configurada para que el emulador se
     * conecte al servidor, debido a limitaciones DNS del emulador.
     *
     * 1. Abre un terminal en tu ordenador (cmd.exe)
     * 2. Teclea: ping raspi
     * 3. Como tu ordenador *SÍ* puede resolver nombres de dominio en la
     *    red local, verás un ping exitoso con una salida como esta:
     *
     *   Haciendo ping a raspi [X.X.X.X] con 32 bytes de datos:
     *   Respuesta desde X.X.X.X: bytes=32 tiempo=2ms TTL=64
     *   Respuesta desde X.X.X.X: bytes=32 tiempo=2ms TTL=64
     *   Respuesta desde X.X.X.X: bytes=32 tiempo=3ms TTL=64
     *   Respuesta desde X.X.X.X: bytes=32 tiempo=2ms TTL=64
     *
     * 4. REEMPLAZA en la siguiente VARIABLE la palabra 'raspi'
     *    por X.X.X.X, sea cual sea.
     *
     * Una vez configurado con éxito, no habrá que repetirlo.
     */
    public static String name = "http://192.168.0.99:8000";
}
