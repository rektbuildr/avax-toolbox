/*
 *
 * AVAX Toolbox - An Avalanche Toolbox
 * Copyright (C) 2023 AVAX Buildr @avaxbuildr
 *
 *
 * For more information, visit:
 * https://crypto.bi
 * https://avax.to
 * https://twitter.com/avaxbuildr
 *
 *
 **/
package to.avax.toolbox.gui.wallet;

import to.avax.avalanche.wallet.HdHelper;
import to.avax.avalanche.wallet.HdWalletCore;
import to.avax.avalanche.wallet.MnemonicPhrase;
import to.avax.avalanche.wallet.MnemonicWallet;
import to.avax.toolbox.gui.AvaxtoPanel;
import to.avax.toolbox.gui.ToolboxFrame;
import to.avax.toolbox.gui.wallet.addresses.AvaxtoWalletAddressPanel;

import javax.swing.*;
import java.awt.*;

public class AvaxtoWalletPanel extends AvaxtoPanel {

    JLabel statusLabel;
    private MnemonicPhrase mnemonic;
    final private JPanel centralPanel;
    JPanel unloggedPanel;
    HdWalletCore wallet;
    AvaxtoWalletAddressPanel xAddressesPanel;
    AvaxtoWalletAddressPanel pAddressesPanel;

    private final static int DEFAULT_ADDRESS_COUNT = 20;
    AvaxtoWalletMainMenu jmb;

    public void listPAddresses() {
        pAddressesPanel.listAddresses();
        centralPanel.removeAll();
        centralPanel.add(pAddressesPanel, BorderLayout.CENTER);
        centralPanel.revalidate();
    }

    public void listXAddresses() {
        xAddressesPanel.listAddresses();
        centralPanel.removeAll();
        centralPanel.add(xAddressesPanel, BorderLayout.CENTER);
        centralPanel.revalidate();
    }

    // has a mnemonic or privkey and can be activated
    private void activateWalletPane(){
        centralPanel.removeAll();
        centralPanel.add(new JPanel());
        statusLabel.setText("Wallet ready");
    }
    public AvaxtoWalletPanel(ToolboxFrame tf) {
        super(tf);
        setLayout(new BorderLayout());

        centralPanel = new JPanel();
        centralPanel.setLayout(new BorderLayout());

        jmb = new AvaxtoWalletMainMenu(this);
        add(jmb, BorderLayout.NORTH);

        JPanel unloggedInternalPanel = new JPanel();
        unloggedPanel = new JPanel();
        unloggedPanel.setLayout(new BorderLayout());

        JLabel jpjl = new JLabel("Enter mnemonic");
        JTextArea jpjta = new JTextArea(3, 60);
        JButton jpbut = new JButton("Go >>");

        jpbut.addActionListener( e -> {

            if (!MnemonicWallet.validateMnemonic(jpjta.getText())) {
                JOptionPane.showMessageDialog(this, "Invalid mnemonic phrase. Unable to open wallet.", "Wallet Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.mnemonic = new MnemonicPhrase(jpjta.getText());
            this.wallet = new MnemonicWallet(this.mnemonic.getValue());

            unloggedPanel.setVisible(false);
            activateWalletPane();
        });

        unloggedInternalPanel.add(jpjl);
        unloggedInternalPanel.add(jpjta);
        unloggedInternalPanel.add(jpbut);

        unloggedPanel.add(unloggedInternalPanel, BorderLayout.LINE_START);

        xAddressesPanel = new AvaxtoWalletAddressPanel(this, DEFAULT_ADDRESS_COUNT, (page, addressCount) -> {
            int index = (page * addressCount);
            int lastIndex = index + addressCount;

            StringBuilder sb = new StringBuilder();
            HdHelper hdh = wallet.getExternalHelper();

            for (; index<lastIndex; index++) {
                String address = hdh.getAddressForIndex(index);
                sb.append(address);
                sb.append("\n");
            }

            return sb.toString();
        });

        pAddressesPanel = new AvaxtoWalletAddressPanel(this, DEFAULT_ADDRESS_COUNT, (page, addressCount) -> {
            int index = (page * addressCount);
            int lastIndex = index + addressCount;

            StringBuilder sb = new StringBuilder();
            HdHelper hdh = wallet.getPlatformHelper();
            for (; index<lastIndex; index++) {
                String address = hdh.getAddressForIndex(index);
                sb.append(address);
                sb.append("\n");
            }

            return sb.toString();
        });


        centralPanel.add(unloggedPanel, BorderLayout.CENTER);
        add(centralPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Wallet login.");
        add(statusLabel, BorderLayout.SOUTH);


    }

    /**
     * Construct a new MnemonicWallet helper
     * */
    public AvaxtoWalletPanel(ToolboxFrame tf, String mnemonic) {
        this(tf);

        if (!MnemonicWallet.validateMnemonic(mnemonic)) {
            JOptionPane.showMessageDialog(this, "Invalid mnemonic phrase. Unable to open wallet.", "Wallet Error", JOptionPane.ERROR_MESSAGE);
            tf.getMainPane().remove(this);
            return;
        }

        this.mnemonic = new MnemonicPhrase(mnemonic);
        this.wallet = new MnemonicWallet(this.mnemonic.getValue());

        activateWalletPane();
    }
}
