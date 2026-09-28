package ar.edu.gestiontecnica.servicio;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SeguridadClave {
    private static final int ITERACIONES = 600000;

    public static String generar(char[] clave) throws GeneralSecurityException {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        byte[] hash = calcular(clave, sal, ITERACIONES);
        return ITERACIONES + ":" + Base64.getEncoder().encodeToString(sal)
                + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(char[] clave, String valorGuardado) throws GeneralSecurityException {
        // El formato es: iteraciones:salBase64:hashBase64.
        try {
            String[] partes = valorGuardado.split(":");
            if (partes.length != 3) { return false; }
            int iteraciones = Integer.parseInt(partes[0]);
            if (iteraciones < 100000 || iteraciones > 2000000) { return false; }
            byte[] sal = Base64.getDecoder().decode(partes[1]);
            byte[] esperado = Base64.getDecoder().decode(partes[2]);
            if (sal.length < 16 || esperado.length != 32) { return false; }
            byte[] obtenido = calcular(clave, sal, iteraciones);
            return MessageDigest.isEqual(esperado, obtenido);
        } catch (IllegalArgumentException | NullPointerException error) {
            return false;
        }
    }

    private static byte[] calcular(char[] clave, byte[] sal, int iteraciones)
            throws GeneralSecurityException {
        PBEKeySpec especificacion = new PBEKeySpec(clave, sal, iteraciones, 256);
        try {
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return fabrica.generateSecret(especificacion).getEncoded();
        } finally {
            especificacion.clearPassword();
        }
    }
}
