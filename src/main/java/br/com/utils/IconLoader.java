package br.com.utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.io.File;
import java.text.Normalizer;

public class IconLoader {
    
    private static final String ICONS_PATH = "src/main/resources/icons/";
    
    private static final String RESOURCE_PATH = "/icons/";

    
    public static ImageIcon loadIcon(String iconName, int width, int height) {
        
        ImageIcon icon = tryLoadIcon(iconName, width, height);
        if (icon != null) {
            return icon;
        }

        
        icon = tryLoadSimilarFile(iconName, width, height);
        if (icon != null) {
            return icon;
        }

        return new ImageIcon();
    }

    
    private static ImageIcon tryLoadIcon(String iconName, int width, int height) {
        if (iconName == null || iconName.trim().isEmpty()) {
            return null;
        }

        try {
            URL resource = IconLoader.class.getResource(RESOURCE_PATH + iconName);
            if (resource != null) {
                return scale(new ImageIcon(resource), width, height);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícone do classpath: " + iconName);
        }

        try {
            File iconFile = new File(ICONS_PATH + iconName);
            if (iconFile.exists()) {
                return scale(new ImageIcon(iconFile.getAbsolutePath()), width, height);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícone do arquivo: " + iconName);
        }

        return null;
    }

    
    private static ImageIcon tryLoadFirstAvailable(int width, int height, String... names) {
        for (String name : names) {
            ImageIcon icon = tryLoadIcon(name, width, height);
            if (icon != null) {
                return icon;
            }
        }
        return new ImageIcon();
    }

    
    private static ImageIcon tryLoadSimilarFile(String iconName, int width, int height) {
        try {
            File dir = new File(ICONS_PATH);
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
            if (files == null) {
                return null;
            }

            
            String target = normalizar(iconName);
            for (File file : files) {
                
                String current = normalizar(file.getName());
                if (current.equals(target) || current.contains(target) || target.contains(current)) {
                    return scale(new ImageIcon(file.getAbsolutePath()), width, height);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar ícone parecido: " + iconName);
        }
        return null;
    }

    
    private static ImageIcon scale(ImageIcon icon, int width, int height) {
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    
    private static String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento
                .replace("#U2014", "")
                .replace("—", "")
                .replace("-", "")
                .replace("_", "")
                .replace(" ", "")
                .replace(".", "")
                .toLowerCase();
    }

    
    public static ImageIcon loadCarIcon(int width, int height) {
        return tryLoadFirstAvailable(width, height,
                "carro.png",
                "#U2014Pngtree#U2014vector car icon_3989896.png",
                "—Pngtree—vector car icon_3989896.png");
    }

    
    public static ImageIcon loadTruckIcon(int width, int height) {
        return tryLoadFirstAvailable(width, height,
                "caminhao.png",
                "#U2014Pngtree#U2014black line drawing delivery truck_4467470.png",
                "—Pngtree—black line drawing delivery truck_4467470.png",
                "delivery-truck.png");
    }

    
    public static ImageIcon loadMotoIcon(int width, int height) {
        return loadIcon("moto.png", width, height);
    }

    
    public static ImageIcon loadVanIcon(int width, int height) {
        return loadIcon("delivery-truck.png", width, height);
    }

    public static ImageIcon loadCaminhoneteIcon(int width, int height) {
        return loadIcon("caminhonete.png", width, height);
    }

    public static ImageIcon loadOnibusIcon(int width, int height) {
        return loadIcon("onibus.png", width, height);
    }

    
    public static ImageIcon loadCombustivelIcon(int width, int height) {
        return loadIcon("bomba-de-gasolina.png", width, height);
    }

    public static ImageIcon loadSeguroIcon(int width, int height) {
        return loadIcon("escudo-seguro.png", width, height);
    }

    public static ImageIcon loadIPVAIcon(int width, int height) {
        return loadIcon("pngwing.com.png", width, height);
    }

    public static ImageIcon loadLavagemIcon(int width, int height) {
        return loadIcon("pngegg.png", width, height);
    }

    public static ImageIcon loadManutencaoIcon(int width, int height) {
        return loadIcon("manutencao.png", width, height);
    }

    public static ImageIcon loadSearchIcon(int width, int height) {
        return loadIcon("motor-de-busca.png", width, height);
    }

    public static ImageIcon loadTableIcon(int width, int height) {
        return loadIcon("tabela.png", width, height);
    }

    public static ImageIcon loadIconForType(String tipo, int width, int height) {
        if (tipo == null) return new ImageIcon();

        
        String lowerType = normalizar(tipo);
        if (lowerType.contains("carro") || lowerType.contains("car")) {
            return loadCarIcon(width, height);
        } else if (lowerType.contains("moto")) {
            return loadMotoIcon(width, height);
        } else if (lowerType.contains("caminhonete") || lowerType.contains("pickup")) {
            return loadCaminhoneteIcon(width, height);
        } else if (lowerType.contains("onibus") || lowerType.contains("bus")) {
            return loadOnibusIcon(width, height);
        } else if (lowerType.contains("caminhao") || lowerType.contains("truck")) {
            return loadTruckIcon(width, height);
        } else if (lowerType.contains("van")) {
            return loadVanIcon(width, height);
        }

        return new ImageIcon();
    }

    
    public static ImageIcon loadIconForExpenseType(String tipo, int width, int height) {
        if (tipo == null) return new ImageIcon();

        
        String lowerType = normalizar(tipo);
        if (lowerType.contains("combustivel")) {
            return loadCombustivelIcon(width, height);
        } else if (lowerType.contains("seguro")) {
            return loadSeguroIcon(width, height);
        } else if (lowerType.contains("ipva")) {
            return loadIPVAIcon(width, height);
        } else if (lowerType.contains("lavagem")) {
            return loadLavagemIcon(width, height);
        } else if (lowerType.contains("manutencao")) {
            return loadManutencaoIcon(width, height);
        } else if (lowerType.contains("multa")) {
            return loadIcon("notas.png", width, height);
        }

        return new ImageIcon();
    }
}