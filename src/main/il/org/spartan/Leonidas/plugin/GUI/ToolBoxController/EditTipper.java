package il.org.spartan.Leonidas.plugin.GUI.ToolBoxController;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import fluent.ly.note;
import il.org.spartan.Leonidas.auxilary_layer.Existence;
import il.org.spartan.Leonidas.plugin.GUI.LeonidasIcon;
import il.org.spartan.Leonidas.plugin.Toolbox;
import il.org.spartan.Leonidas.plugin.UserControlledMatcher;
import il.org.spartan.Leonidas.plugin.UserControlledReplacer;
import il.org.spartan.Leonidas.plugin.UserControlledTipper;
import il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.GenericEncapsulator;
import il.org.spartan.Leonidas.plugin.tippers.LeonidasTipper;
import il.org.spartan.Leonidas.plugin.tippers.leonidas.LeonidasTipperDefinition;
import il.org.spartan.Leonidas.plugin.tipping.Tipper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Amir Sagiv, Anna Belozovsky
 * @since 14-05-2017
 */
public class EditTipper extends JFrame {
    private JPanel mainPanel;
    private JButton applyButton;
    private JButton closeButton;
    private JPanel tempPane;
    private JScrollPane TablePanel;
    private JLabel nameLabel;
    private ComponentJTable table;
    private Tipper currentTip;

    public EditTipper(String tipperName) {
        super("Edit Tipper");
        LeonidasIcon.apply(this);
        nameLabel.setText(tipperName);
        currentTip = null;
        List<Tipper> tippers = Toolbox.getInstance().getAllTippers();
        List<LeonidasTipperDefinition> tippersInstances=Toolbox.getInstance().getAllTipperInstances();
        for (Tipper tip : tippers) {
            //           JOptionPane.showMessageDialog(this, tip.getClass().getSimpleName() + ", " + tipperName);
            if (tip.name().equals(tipperName)) {
                currentTip = tip;
                break;
            }
        }

        if(!canBeEdited(tippersInstances)){
            return;
        }


        table = new ComponentJTable();
        ((DefaultTableModel) table.getModel()).setRowCount(100); //TODO: MAGIC NUMBER
        LeonidasTipper lt = (LeonidasTipper)currentTip;
        List<GenericEncapsulator> tipperMatcherRoots = lt.getMatcher().getAllRoots().stream().map(root -> LeonidasTipper.getGenericElements(root)).flatMap(list-> list.stream()).collect(Collectors.toList());

        int currRow = 0;
        currRow = buildTableFields(tipperMatcherRoots,currRow,true);
        List<GenericEncapsulator> tipperReplacerRoots = lt.getReplacer().getAllRoots().stream().map(root -> LeonidasTipper.getGenericElements(root)).flatMap(list-> list.stream()).collect(Collectors.toList());
        currRow = buildTableFields(tipperReplacerRoots,currRow,false);
        ((DefaultTableModel) table.getModel()).setRowCount(currRow);


        applyButton.addActionListener(e -> applyListener());
        closeButton.addActionListener(e -> {
            this.dispose();
        });
        TablePanel.setViewportView(table);
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(800, 600));
        setResizable(false);
        pack();
        setVisible(true);
    }

    private boolean canBeEdited(List<LeonidasTipperDefinition> tippersInstances) {
        if (currentTip == null || !(currentTip instanceof LeonidasTipper)) {
            JOptionPane.showMessageDialog(this, "The tip: "+ currentTip.name() + " Can't Be Edited. ");
            return false;
        }
        LeonidasTipperDefinition ltd = null;
        for(LeonidasTipperDefinition instance : tippersInstances){
            if(instance.getClass().getSimpleName().equals( currentTip.name())){
                ltd = instance;
            }
        }
        if(ltd == null || !ltd.getClass().isAnnotationPresent(UserControlledTipper.class)){
            JOptionPane.showMessageDialog(this, "The tip: "+ currentTip.name() + " Can't Be Edited. ");
            return false;
        }
        return true;
    }

    private int buildTableFields(List<GenericEncapsulator> tipperRoots,int i,boolean matcher){
        for(GenericEncapsulator root : tipperRoots){
            Field[] fields = root.getClass().getFields();


            for (Field field : fields) {
                if ((matcher && !field.isAnnotationPresent(UserControlledMatcher.class)) ||
                        (!matcher && !field.isAnnotationPresent(UserControlledReplacer.class))) {
                    continue;
                }
                Class type = field.getType();
                try {
                    if (type.isPrimitive() && type.getName().equals("boolean")) {
                        table.getModel().setValueAt(new JLabel(field.getName()+" of "+root.getDescription()), i, 0);
                        table.getModel().setValueAt(new JCheckBox("", (Boolean) field.get(root)), i++, 1);
                        continue;
                    }

                    if (type == List.class) {
                        for(Object  element: (List)field.get(root)) {
                            table.getModel().setValueAt(new JLabel(field.getName() + " of " + root.getDescription()), i, 0);
                            table.getModel().setValueAt(new JTextField((String) element), i++, 1);
                        }
                        continue;
                    }
                    if (type == Map.class) {
                        for (Map.Entry<Integer, String> entry : ((Map<Integer,String>)field.get(root)).entrySet())
                        {
                            table.getModel().setValueAt(new JLabel(field.getName() + " of " + root.getDescription()), i, 0);
                            table.getModel().setValueAt(new JTextField((String) entry.getValue()), i++, 1);
                        }
                        continue;
                    }

                    if(type == Existence.class){
                        JComboBox cb = new JComboBox(new Existence[]{Existence.DO_NOT_CARE,Existence.MUST_EXISTS,Existence.DOES_NOT_EXISTS});
                        cb.setSelectedItem(field.get(root));
                        table.getModel().setValueAt(new JLabel(field.getName()+" of "+root.getDescription()), i, 0);
                        table.getModel().setValueAt(cb, i++, 1);
                        continue;
                    }

                    Object obj = type.newInstance();
                    if (obj instanceof String) {
                        if(!((String) field.get(root)).equals("")) {
                            table.getModel().setValueAt(new JLabel(field.getName() + " of " + root.getDescription()), i, 0);
                            table.getModel().setValueAt(new JTextField((String) field.get(root)), i++, 1);
                        }
                        continue;
                    }

                } catch (Exception e) {
                    note.bug(e);
                }
            }
        }
        return i;
    }

    private int updateFieldsFromTable(List<GenericEncapsulator> tipperRoots,int i,boolean matcher){
        for(GenericEncapsulator root : tipperRoots){
            Field[] fields = root.getClass().getFields();


            for (Field field : fields) {
                if ((matcher && !field.isAnnotationPresent(UserControlledMatcher.class)) ||
                        (!matcher && !field.isAnnotationPresent(UserControlledReplacer.class))) {
                    continue;
                }
                Class type = field.getType();
                try {
                    if (type.isPrimitive() && type.getName().equals("boolean")) {
                        field.set(root, ((JCheckBox)table.getValueAt(i++,1)).isSelected());
                        continue;
                    }

                    if (type == List.class) {
                        int listSize = ((List)field.get(root)).size();
                        ((List)field.get(root)).clear();
                        for(int j = 0; j < listSize; j++){
                            ((List)field.get(root)).add(((JTextField)table.getValueAt(i++,1)).getText());
                        }
                        continue;
                    }
                    if (type == Map.class) {
                        for (Map.Entry<Integer, String> entry : ((Map<Integer,String>)field.get(root)).entrySet())
                        {
                            ((Map<Integer,String>)field.get(root)).put(entry.getKey(),((JTextField)table.getValueAt(i,1)).getText());
                        }
                        continue;
                    }

                    if(type == Existence.class){
                        field.set(root, ((JComboBox)table.getValueAt(i++,1)).getSelectedItem());
                        continue;
                    }


                    Object obj = type.newInstance();
                    if (obj instanceof String) {
                        field.set(root, ((JTextField)table.getValueAt(i++,1)).getText());
                        continue;
                    }

                } catch (Exception e) {
                    note.bug(e);
                }
            }
        }
        return i;
    }

    private void applyListener() {
        LeonidasTipper lt = (LeonidasTipper)currentTip;
        List<GenericEncapsulator> tipperMatcherRoots = lt.getMatcher().getAllRoots().stream().map(root -> LeonidasTipper.getGenericElements(root)).flatMap(list-> list.stream()).collect(Collectors.toList());

        int currRow = 0;
        currRow = updateFieldsFromTable(tipperMatcherRoots,currRow,true);

        List<GenericEncapsulator> tipperReplacerRoots = lt.getReplacer().getAllRoots().stream().map(root -> LeonidasTipper.getGenericElements(root)).flatMap(list-> list.stream()).collect(Collectors.toList());
        updateFieldsFromTable(tipperReplacerRoots,currRow,false);

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        nameLabel = new JLabel();
        nameLabel.setFont(new Font(nameLabel.getFont().getName(), nameLabel.getFont().getStyle(), 28));
        nameLabel.setText("Label");
        mainPanel.add(nameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tempPane = new JPanel();
        tempPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(tempPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        TablePanel = new JScrollPane();
        tempPane.add(TablePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        applyButton = new JButton();
        applyButton.setText("Apply");
        panel1.add(applyButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        closeButton = new JButton();
        closeButton.setText("Close");
        panel1.add(closeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
