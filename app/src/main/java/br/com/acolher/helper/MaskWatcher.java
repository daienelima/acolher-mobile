package br.com.acolher.helper;

import android.text.Editable;
import android.text.TextWatcher;

public class MaskWatcher implements TextWatcher {
    private boolean isRunning = false;
    private boolean isDeleting = false;
    private final String mask;

    public MaskWatcher(String mask) {
        this.mask = mask;
    }

    public static MaskWatcher buildCpf() {
        return new MaskWatcher("###.###.###-##");
    }
    public static MaskWatcher buildCnpj() {
        return new MaskWatcher("##.###.###/####-##");
    }

    public static MaskWatcher buildCep() {
        return new MaskWatcher("##.###-###");
    }

    public static MaskWatcher buildFone() {
        return new MaskWatcher("(##) #####-####");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        isDeleting = count > after;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (isRunning || isDeleting) {
            return;
        }
        isRunning = true;

        int editableLength = editable.length();
        if (editableLength < mask.length()) {
            if (mask.charAt(editableLength) != '#') {
                editable.append(mask.charAt(editableLength));
            } else if (mask.charAt(editableLength-1) != '#') {
                editable.insert(editableLength-1, mask, editableLength-1, editableLength);
            }
        }

        isRunning = false;
    }

    /**
     * Adiconar Mascara
     * @param textoAFormatar
     * @param mask
     * @return
     */
    public static String addMask(final String textoAFormatar, final String mask){
        String formatado = "";
        int i = 0;
        for (char m : mask.toCharArray()) {
            if (m != '#') { // se não for um #, vamos colocar o caracter informado na máscara
                formatado += m;
                continue;
            }
            // Senão colocamos o valor que será formatado
            try {
                formatado += textoAFormatar.charAt(i);
            } catch (Exception e) {
                break;
            }
            i++;
        }
        return formatado;
    }


    /**
     * Remover mascara
     * @param textoAFormatar
     * @return
     */
    public static String removeMask(String textoAFormatar){
        String formatado = "";
        formatado = textoAFormatar.replaceAll("/[^0-9]+/g","");
        return formatado;
    }
}