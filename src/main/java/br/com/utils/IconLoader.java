package br.com.utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.io.File;
import java.text.Normalizer;

public class IconLoader {
    // Caminho utilizado para buscar ícones diretamente na pasta do projeto
    private static final String ICONS_PATH = "src/main/resources/icons/";
    // Caminho utilizado para buscar ícones empacotados nos recursos da aplicação
    private static final String RESOURCE_PATH = "/icons/";

    // Tenta carregar um ícone pelo nome informado
    public static ImageIcon loadIcon(String iconName, int width, int height) {
        // Procura o ícone pelo nome exato
        ImageIcon icon = tryLoadIcon(iconName, width, height);
        if (icon != null) {
            return icon;
        }

        // Caso não encontre, procura um arquivo com nome semelhante
        icon = tryLoadSimilarFile(iconName, width, height);
        if (icon != null) {
            return icon;
        }

        return new ImageIcon();
    }

    // Tenta carregar um ícone pelo classpath ou diretamente da pasta de recursos
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

    // Retorna o primeiro ícone encontrado dentre as opções informadas
    private static ImageIcon tryLoadFirstAvailable(int width, int height, String... names) {
        for (String name : names) {
            ImageIcon icon = tryLoadIcon(name, width, height);
            if (icon != null) {
                return icon;
            }
        }
        return new ImageIcon();
    }

    // Procura um arquivo com nome semelhante ao solicitado
    private static ImageIcon tryLoadSimilarFile(String iconName, int width, int height) {
        try {
            File dir = new File(ICONS_PATH);
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
            if (files == null) {
                return null;
            }

            // Normaliza o nome para facilitar comparações
            String target = normalizar(iconName);
            for (File file : files) {
                // Verifica se os nomes são iguais ou suficientemente parecidos
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

    // Redimensiona a imagem para o tamanho desejado
    private static ImageIcon scale(ImageIcon icon, int width, int height) {
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // Remove acentos, espaços e caracteres especiais para facilitar comparações
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

    // Carrega o ícone utilizado para veículos do tipo carro
    public static ImageIcon loadCarIcon(int width, int height) {
        return tryLoadFirstAvailable(width, height,
                "carro.png",
                "#U2014Pngtree#U2014vector car icon_3989896.png",
                "—Pngtree—vector car icon_3989896.png");
    }

    // Carrega o ícone utilizado para caminhões
    public static ImageIcon loadTruckIcon(int width, int height) {
        return tryLoadFirstAvailable(width, height,
                "caminhao.png",
                "#U2014Pngtree#U2014black line drawing delivery truck_4467470.png",
                "—Pngtree—black line drawing delivery truck_4467470.png",
                "delivery-truck.png");
    }

    // Carrega o ícone utilizado para motocicletas
    public static ImageIcon loadMotoIcon(int width, int height) {
        return loadIcon("moto.png", width, height);
    }

    // Carrega o ícone utilizado para vans
    public static ImageIcon loadVanIcon(int width, int height) {
        return loadIcon("delivery-truck.png", width, height);
    }

    // Retorna o ícone correspondente ao tipo de veículo informado
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

        // Normaliza o texto para facilitar a identificação do tipo
        String lowerType = normalizar(tipo);
        if (lowerType.contains("carro") || lowerType.contains("car")) {
            return loadCarIcon(width, height);
        } else if (lowerType.contains("moto")) {
            return loadMotoIcon(width, height);
        } else if (lowerType.contains("caminhao") || lowerType.contains("truck")) {
            return loadTruckIcon(width, height);
        } else if (lowerType.contains("van")) {
            return loadVanIcon(width, height);
        }

        return new ImageIcon();
    }

    // Retorna o ícone correspondente ao tipo de despesa informado
    public static ImageIcon loadIconForExpenseType(String tipo, int width, int height) {
        if (tipo == null) return new ImageIcon();

        // Identifica a categoria da despesa e retorna o ícone correspondente
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