package br.com.ui.components;

import br.com.model.Veiculo;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.utils.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class VehicleCard extends RoundedPanel {
    private Veiculo veiculo;
    private List<VehicleCardListener> listeners = new ArrayList<>();
    private boolean isHovered = false;
    private static final Color BORDER_COLOR = ModernColors.BORDER_GRAY;
    private static final Color HOVER_COLOR = ModernColors.CARD_HOVER;
    private static final Color HOVER_BORDER_COLOR = ModernColors.PRIMARY_BLUE;

    public interface VehicleCardListener {
        void onEditClicked(Veiculo veiculo);
        void onDeleteClicked(Veiculo veiculo);
        void onCardClicked(Veiculo veiculo);
    }

    public VehicleCard(Veiculo veiculo) {
        super(16, ModernColors.WHITE);
        this.veiculo = veiculo;
        setPreferredSize(new Dimension(286, 194));
        setLayout(new BorderLayout(10, 8));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 14, 16));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                setBackgroundColor(HOVER_COLOR);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                setBackgroundColor(ModernColors.WHITE);
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                notifyCardClicked();
            }
        };

        addMouseListener(mouseAdapter);
        initializeComponents();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int shadowSize = 5;
        int arcRadius = 16;
        g2d.setColor(isHovered ? HOVER_BORDER_COLOR : BORDER_COLOR);
        g2d.setStroke(new BasicStroke(isHovered ? 2.2f : 1.2f));
        g2d.drawRoundRect(2, 2, getWidth() - shadowSize - 4, getHeight() - shadowSize - 4, arcRadius, arcRadius);

        g2d.dispose();
    }

    private void initializeComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setOpaque(false);

        JPanel iconWrapper = criarPainelIcone();
        topPanel.add(iconWrapper, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel placaLabel = new JLabel(veiculo.getPlaca() + " • " + veiculo.getTipo());
        placaLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        placaLabel.setForeground(ModernColors.NAVY);

        JLabel modeloLabel = new JLabel(veiculo.getMarca() + " " + veiculo.getModelo());
        modeloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        modeloLabel.setForeground(ModernColors.TEXT_GRAY);

        JLabel anoLabel = new JLabel("Ano: " + veiculo.getFabricateYear());
        anoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        anoLabel.setForeground(ModernColors.TEXT_GRAY);

        infoPanel.add(placaLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(modeloLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(anoLabel);

        topPanel.add(infoPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        JLabel statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 11, 5, 11));

        if (veiculo.getAtivo()) {
            statusLabel.setText("● Ativo");
            statusLabel.setBackground(ModernColors.SUCCESS_GREEN);
        } else {
            statusLabel.setText("● Inativo");
            statusLabel.setBackground(ModernColors.TEXT_GRAY);
        }
        statusLabel.setForeground(ModernColors.WHITE);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        ModernButton editBtn = new ModernButton("Editar", ModernColors.PRIMARY_BLUE);
        editBtn.setPreferredSize(new Dimension(78, 31));
        editBtn.addActionListener(e -> notifyEditClicked());

        ModernButton deleteBtn = new ModernButton("Excluir", ModernColors.DANGER_RED);
        deleteBtn.setPreferredSize(new Dimension(78, 31));
        deleteBtn.addActionListener(e -> notifyDeleteClicked());

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel criarPainelIcone() {
        JPanel iconWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(ModernColors.LIGHT_BLUE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        iconWrapper.setOpaque(false);
        iconWrapper.setPreferredSize(new Dimension(52, 52));
        iconWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        ImageIcon icon = IconLoader.loadIconForType(veiculo.getTipo(), 36, 36);
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconWrapper.add(iconLabel, BorderLayout.CENTER);
        return iconWrapper;
    }

    public void addListener(VehicleCardListener listener) {
        listeners.add(listener);
    }

    private void notifyEditClicked() {
        for (VehicleCardListener listener : listeners) {
            listener.onEditClicked(veiculo);
        }
    }

    private void notifyDeleteClicked() {
        for (VehicleCardListener listener : listeners) {
            listener.onDeleteClicked(veiculo);
        }
    }

    private void notifyCardClicked() {
        for (VehicleCardListener listener : listeners) {
            listener.onCardClicked(veiculo);
        }
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }
}
