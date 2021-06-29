package com.hiveworkshop.rms.ui.application;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlag;
import com.hiveworkshop.rms.filesystem.GameDataFileSystem;
import com.hiveworkshop.rms.parsers.blp.BLPHandler;
import com.hiveworkshop.rms.parsers.slk.DataTable;
import com.hiveworkshop.rms.ui.application.edit.uv.panel.UVPanel;
import com.hiveworkshop.rms.ui.application.scripts.AnimationTransfer;
import com.hiveworkshop.rms.ui.application.tools.EditTexturesPopupPanel;
import com.hiveworkshop.rms.ui.application.tools.KeyframeCopyPanel;
import com.hiveworkshop.rms.ui.browsers.jworldedit.WEString;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.UnitEditorTree;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.datamodel.MutableObjectData;
import com.hiveworkshop.rms.ui.browsers.model.ModelOptionPanel;
import com.hiveworkshop.rms.ui.browsers.mpq.MPQBrowser;
import com.hiveworkshop.rms.ui.browsers.unit.UnitOptionPanel;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelPanel;
import com.hiveworkshop.rms.ui.gui.modeledit.util.TransferActionListener;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.preferences.SaveProfile;
import com.hiveworkshop.rms.ui.util.LanguageReader;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;import java.util.List;
import java.util.Queue;
import java.util.*;

import static com.hiveworkshop.rms.ui.application.MenuCreationUtils.createAndAddMenuItem;
import static com.hiveworkshop.rms.ui.application.MenuCreationUtils.createMenu;

public class MenuBar {
    static JMenuBar menuBar;
    static MainPanel mainPanel;

    public static final ImageIcon AnimIcon = RMSIcons.AnimIcon;

    static JMenu recentMenu;
    static JMenu toolsMenu;
    static JMenu windowMenu;
    static List<MenuBar.RecentItem> recentItems = new ArrayList<>();

    private static final ResourceBundle resourceBundle = LanguageReader.getRb();

    public static JMenuBar createMenuBar(MainPanel mainPanel) {
        MenuBar.mainPanel = mainPanel;
        // Create my menu bar
        menuBar = new JMenuBar();

        // Build the file menu
        JMenu fileMenu = createMenu(resourceBundle.getString("file"), KeyEvent.VK_F, resourceBundle.getString("file.button.description"));
        menuBar.add(fileMenu);

        JMenu editMenu = createMenu(resourceBundle.getString("edit"), KeyEvent.VK_E, resourceBundle.getString("edit.button.description"));
        menuBar.add(editMenu);

//        mainPanel.toolsMenu = createMenu("Tools", KeyEvent.VK_T, "Allows the user to use various model editing tools. (You must open a model before you may use this menu.)");
//        mainPanel.toolsMenu.setEnabled(false);
//        menuBar.add(mainPanel.toolsMenu);
        toolsMenu = createMenu(resourceBundle.getString("tools"), KeyEvent.VK_T, resourceBundle.getString("tools.button.description"));
        toolsMenu.setEnabled(false);
        menuBar.add(toolsMenu);

        JMenu viewMenu = createMenu(resourceBundle.getString("view"), -1, resourceBundle.getString("view.button.description"));
        menuBar.add(viewMenu);

        mainPanel.teamColorMenu = createMenu(resourceBundle.getString("team.color"), -1, resourceBundle.getString("team.color.button.description"));
        menuBar.add(mainPanel.teamColorMenu);

        mainPanel.directoryChangeNotifier.subscribe(() -> {
            GameDataFileSystem.refresh(SaveProfile.get().getDataSources());
            // cache priority order...
            UnitOptionPanel.dropRaceCache();
            DataTable.dropCache();
            ModelOptionPanel.dropCache();
            WEString.dropCache();
            BLPHandler.get().dropCache();
            mainPanel.teamColorMenu.removeAll();
            createTeamColorMenuItems(mainPanel);
            traverseAndReloadData(mainPanel.rootWindow);
        });
        createTeamColorMenuItems(mainPanel);

        JMenu windowMenu = createMenu(resourceBundle.getString("window"), KeyEvent.VK_W, resourceBundle.getString("windows.button.description"));
//        mainPanel.windowMenu = windowMenu;
        MenuBar.windowMenu = windowMenu;
        menuBar.add(windowMenu);

        fillWindowsMenu(mainPanel, windowMenu);

        JMenu addMenu = createMenu(resourceBundle.getString("add"), KeyEvent.VK_A, resourceBundle.getString("add.button.description"));
        menuBar.add(addMenu);

        fillAddMenu(mainPanel, addMenu);

        JMenu scriptsMenu = createMenu(resourceBundle.getString("scripts"), KeyEvent.VK_A, resourceBundle.getString("scripts.button.description"));
        menuBar.add(scriptsMenu);

        fillScriptsMenu(mainPanel, scriptsMenu);

        final JMenuItem fixReteraLand = new JMenuItem(resourceBundle.getString("fix.retera.land"));

        fixReteraLand.setMnemonic(KeyEvent.VK_A);
        fixReteraLand.addActionListener(e -> {
            final EditableModel currentMDL = mainPanel.currentMDL();
            for (final Geoset geo : currentMDL.getGeosets()) {
                final Animation anim = new Animation(new ExtLog(currentMDL.getExtents()));
                geo.add(anim);
            }
        });
//		scriptsMenu.add(fixReteraLand);

        JMenu aboutMenu = createMenu(resourceBundle.getString("help"), KeyEvent.VK_H, "");
        menuBar.add(aboutMenu);


//        mainPanel.recentMenu = createMenu("Open Recent", KeyEvent.VK_R, "Allows you to access recently opened files.");
//        mainPanel.recentMenu.add(new JSeparator());
//        createAndAddMenuItem("Clear", mainPanel.recentMenu, KeyEvent.VK_C, e -> MenuBarActions.clearRecent(mainPanel));
        recentMenu = createMenu(resourceBundle.getString("open.recent"), KeyEvent.VK_R, resourceBundle.getString("open.recent.button.description"));
        recentMenu.add(new JSeparator());
        createAndAddMenuItem(resourceBundle.getString("clear"), recentMenu, KeyEvent.VK_C, e -> MenuBarActions.clearRecent(mainPanel));

        updateRecent();

        fillAboutMenu(mainPanel, aboutMenu);

        fillToolsMenu(mainPanel);

        fillViewMenu(mainPanel, viewMenu);

        fillFileMenu(mainPanel, fileMenu);


        fillEditMenu(mainPanel, editMenu);

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            menuBar.getMenu(i).getPopupMenu().setLightWeightPopupEnabled(false);
        }
        return menuBar;
    }

    private static void fillFileMenu(MainPanel mainPanel, JMenu fileMenu) {
        FileDialog fileDialog = new FileDialog(mainPanel);
        createAndAddMenuItem(resourceBundle.getString("new"), fileMenu, KeyEvent.VK_N, KeyStroke.getKeyStroke("control N"), e -> MenuBarActions.newModel(mainPanel));

//        createAndAddMenuItem("Open", fileMenu, KeyEvent.VK_O, KeyStroke.getKeyStroke("control O"), e -> MenuBarActions.onClickOpen(mainPanel));
        createAndAddMenuItem(resourceBundle.getString("open"), fileMenu, KeyEvent.VK_O, KeyStroke.getKeyStroke("control O"), e -> fileDialog.onClickOpen());

//        fileMenu.add(mainPanel.recentMenu);
        fileMenu.add(recentMenu);

        JMenu fetch = new JMenu(resourceBundle.getString("open.internal"));
        fetch.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(fetch);

        createAndAddMenuItem(resourceBundle.getString("unit"), fetch, KeyEvent.VK_U, KeyStroke.getKeyStroke("control U"), e -> MPQBrowserView.fetchUnit(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("model1"), fetch, KeyEvent.VK_M, KeyStroke.getKeyStroke("control M"), e -> MPQBrowserView.fetchModel(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("object.editor"), fetch, KeyEvent.VK_O, KeyStroke.getKeyStroke("control O"), e -> MPQBrowserView.fetchObject(mainPanel));

        fetch.add(new JSeparator());

        JCheckBoxMenuItem fetchPortraitsToo = new JCheckBoxMenuItem(resourceBundle.getString("fetch.portraits.too"), true);
        fetchPortraitsToo.setMnemonic(KeyEvent.VK_P);
        fetchPortraitsToo.addActionListener(e -> mainPanel.prefs.setLoadPortraits(fetchPortraitsToo.isSelected()));
        fetch.add(fetchPortraitsToo);
        fetchPortraitsToo.setSelected(mainPanel.prefs.isLoadPortraits());

        fileMenu.add(new JSeparator());

        JMenu importMenu = createMenu(resourceBundle.getString("import"), KeyEvent.VK_I);
        fileMenu.add(importMenu);

        createAndAddMenuItem(resourceBundle.getString("from.file"), importMenu, KeyEvent.VK_I, KeyStroke.getKeyStroke("control shift I"), e -> ImportFileActions.importButtonActionRes(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("from.unit"), importMenu, KeyEvent.VK_U, KeyStroke.getKeyStroke("control shift U"), e -> ImportFileActions.importUnitActionRes(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("from.wc3.model"), importMenu, KeyEvent.VK_M, e -> ImportFileActions.importGameModelActionRes(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("from.object.editor"), importMenu, KeyEvent.VK_O, e -> ImportFileActions.importGameObjectActionRes(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("from.workspace"), importMenu, KeyEvent.VK_O, e -> ImportFileActions.importFromWorkspaceActionRes(mainPanel));

//        createAndAddMenuItem("Save", fileMenu, KeyEvent.VK_S, KeyStroke.getKeyStroke("control S"), e -> save(mainPanel));
        createAndAddMenuItem(resourceBundle.getString("save"), fileMenu, KeyEvent.VK_S, KeyStroke.getKeyStroke("control S"), e -> fileDialog.onClickSave());

//        createAndAddMenuItem("Save as", fileMenu, KeyEvent.VK_A, KeyStroke.getKeyStroke("control Q"), e -> MenuBarActions.onClickSaveAs(mainPanel));
        createAndAddMenuItem(resourceBundle.getString("save.as"), fileMenu, KeyEvent.VK_A, KeyStroke.getKeyStroke("control Q"), e -> fileDialog.onClickSaveAs());

        fileMenu.add(new JSeparator());

        createAndAddMenuItem(resourceBundle.getString("export.material.as.texture"), fileMenu, KeyEvent.VK_E, e -> ExportTextureDialog.exportMaterialAsTextures(mainPanel));
        createAndAddMenuItem(resourceBundle.getString("export.texture1"), fileMenu, KeyEvent.VK_E, e -> ExportTextureDialog.exportTextures(mainPanel));

        fileMenu.add(new JSeparator());

        createAndAddMenuItem(resourceBundle.getString("revert"), fileMenu, -1, e -> MPQBrowserView.revert(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("close"), fileMenu, KeyEvent.VK_E, KeyStroke.getKeyStroke("control E"), e -> MenuBarActions.closePanel(mainPanel));

        fileMenu.add(new JSeparator());

        createAndAddMenuItem(resourceBundle.getString("exit"), fileMenu, KeyEvent.VK_E, e -> closeProgram(mainPanel));
    }

    private static void fillEditMenu(final MainPanel mainPanel, JMenu editMenu) {
        mainPanel.undo = new UndoMenuItem(mainPanel, resourceBundle.getString("undo"));
        mainPanel.undo.addActionListener(mainPanel.undoAction);
        mainPanel.undo.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        mainPanel.undo.setEnabled(mainPanel.undo.funcEnabled());
        // undo.addMouseListener(this);
        editMenu.add(mainPanel.undo);

        mainPanel.redo = new RedoMenuItem(mainPanel, resourceBundle.getString("redo"));
        mainPanel.redo.addActionListener(mainPanel.redoAction);
        mainPanel.redo.setAccelerator(KeyStroke.getKeyStroke("control Y"));
        mainPanel.redo.setEnabled(mainPanel.redo.funcEnabled());
        // redo.addMouseListener(this);
        editMenu.add(mainPanel.redo);


        editMenu.add(new JSeparator());
        final JMenu optimizeMenu = createMenu(resourceBundle.getString("optimize"), KeyEvent.VK_O);
        editMenu.add(optimizeMenu);
        createAndAddMenuItem(resourceBundle.getString("linearize.animations"), optimizeMenu, KeyEvent.VK_L, e -> ModelEditActions.linearizeAnimations(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("simplify.keyframes.experimental"), optimizeMenu, KeyEvent.VK_K, e -> ModelEditActions.simplifyKeyframes(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("minimize.geosets"), optimizeMenu, KeyEvent.VK_K, e -> minimizeGeoset(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("sort.nodes"), optimizeMenu, KeyEvent.VK_S, e -> sortBones(mainPanel));

        final JMenuItem flushUnusedTexture = new JMenuItem(resourceBundle.getString("flush.unused.texture"));
        flushUnusedTexture.setEnabled(false);
        flushUnusedTexture.setMnemonic(KeyEvent.VK_F);
        optimizeMenu.add(flushUnusedTexture);

        createAndAddMenuItem(resourceBundle.getString("remove.materials.duplicates"), optimizeMenu, KeyEvent.VK_S, e -> removeMaterialDuplicates(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("recalculate.normals"), editMenu, -1, KeyStroke.getKeyStroke("control N"), e -> ModelEditActions.recalculateNormals(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("recalculate.extents"), editMenu, -1, KeyStroke.getKeyStroke("control shift E"), e -> ModelEditActions.recalculateExtents(mainPanel));

        editMenu.add(new JSeparator());
        final TransferActionListener transferActionListener = new TransferActionListener();
        final ActionListener copyActionListener = e -> copyCutPast(mainPanel, transferActionListener, e);


        createAndAddMenuItem(resourceBundle.getString("cut"), editMenu, KeyStroke.getKeyStroke("control X"), (String) TransferHandler.getCutAction().getValue(Action.NAME), copyActionListener);

        createAndAddMenuItem(resourceBundle.getString("copy"), editMenu, KeyStroke.getKeyStroke("control C"), (String) TransferHandler.getCopyAction().getValue(Action.NAME), copyActionListener);

        createAndAddMenuItem(resourceBundle.getString("paste"), editMenu, KeyStroke.getKeyStroke("control V"), (String) TransferHandler.getPasteAction().getValue(Action.NAME), copyActionListener);

        createAndAddMenuItem(resourceBundle.getString("duplicate"), editMenu, -1, KeyStroke.getKeyStroke("control D"), mainPanel.cloneAction);

        editMenu.add(new JSeparator());

//        createAndAddMenuItem("Snap Vertices", editMenu, -1, KeyStroke.getKeyStroke("control shift W"), e -> MenuBarActions.getSnapVerticiesAction(mainPanel));
        createAndAddMenuItem(resourceBundle.getString("snap.vertices"), editMenu, -1, KeyStroke.getKeyStroke("control shift W"), e -> ModelEditActions.snapVertices(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("snap.normals"), editMenu, -1, KeyStroke.getKeyStroke("control L"), e -> ModelEditActions.snapNormals(mainPanel));

        editMenu.add(new JSeparator());

        createAndAddMenuItem(resourceBundle.getString("select.all"), editMenu, -1, KeyStroke.getKeyStroke("control A"), mainPanel.selectAllAction);

        createAndAddMenuItem(resourceBundle.getString("invert.selection"), editMenu, -1, KeyStroke.getKeyStroke("control I"), mainPanel.invertSelectAction);

        createAndAddMenuItem(resourceBundle.getString("expand.selection"), editMenu, -1, KeyStroke.getKeyStroke("control E"), mainPanel.expandSelectionAction);

        editMenu.addSeparator();

        createAndAddMenuItem(resourceBundle.getString("delete"), editMenu, KeyEvent.VK_D, mainPanel.deleteAction);

        editMenu.addSeparator();
        createAndAddMenuItem(resourceBundle.getString("preferences.window"), editMenu, KeyEvent.VK_P, e -> MenuBarActions.openPreferences(mainPanel));
    }

    private static void fillToolsMenu(MainPanel mainPanel) {
        JMenuItem showMatrices = new JMenuItem("View Selected \"Matrices\"");
        // showMatrices.setMnemonic(KeyEvent.VK_V);
        showMatrices.addActionListener(e -> ModelEditActions.viewMatrices(mainPanel));
        toolsMenu.add(showMatrices);

        JMenuItem insideOut = new JMenuItem(resourceBundle.getString("flip.all.selected.faces"));
        insideOut.setMnemonic(KeyEvent.VK_I);
        insideOut.addActionListener(e -> ModelEditActions.insideOut(mainPanel));
        insideOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
        toolsMenu.add(insideOut);

        JMenuItem insideOutNormals = new JMenuItem(resourceBundle.getString("flip.all.selected.normals"));
        insideOutNormals.addActionListener(e -> ModelEditActions.insideOutNormals(mainPanel));
        toolsMenu.add(insideOutNormals);

        toolsMenu.add(new JSeparator());

        createAndAddMenuItem(resourceBundle.getString("edit.uv.mapping"), toolsMenu, KeyEvent.VK_U, e -> EditUVsPanel.showEditUVs(mainPanel));

        JMenuItem editTextures = new JMenuItem(resourceBundle.getString("edit.textures"));
        editTextures.setMnemonic(KeyEvent.VK_T);
        editTextures.addActionListener(e -> EditTexturesPopupPanel.show(mainPanel));
        toolsMenu.add(editTextures);

        createAndAddMenuItem(resourceBundle.getString("rig.selection"), toolsMenu, KeyEvent.VK_R, KeyStroke.getKeyStroke("control W"), mainPanel.rigAction);

        JMenu tweaksSubmenu = new JMenu(resourceBundle.getString("tweaks"));
        tweaksSubmenu.setMnemonic(KeyEvent.VK_T);
        tweaksSubmenu.getAccessibleContext().setAccessibleDescription(resourceBundle.getString("tweaks.button.description"));
        toolsMenu.add(tweaksSubmenu);
        createAndAddMenuItem(resourceBundle.getString("flip.all.uvs.u"), tweaksSubmenu, KeyEvent.VK_U, e -> ModelEditActions.flipAllUVsU(mainPanel));

        JMenuItem flipAllUVsV = new JMenuItem(resourceBundle.getString("flip.all.uvs.v"));
        // flipAllUVsV.setMnemonic(KeyEvent.VK_V);
        flipAllUVsV.addActionListener(e -> ModelEditActions.flipAllUVsV(mainPanel));
        tweaksSubmenu.add(flipAllUVsV);

        createAndAddMenuItem(resourceBundle.getString("swap.all.uvs.u.for.v"), tweaksSubmenu, KeyEvent.VK_S, e -> ModelEditActions.inverseAllUVs(mainPanel));

        JMenu mirrorSubmenu = new JMenu(resourceBundle.getString("mirror"));
        mirrorSubmenu.setMnemonic(KeyEvent.VK_M);
        mirrorSubmenu.getAccessibleContext().setAccessibleDescription(resourceBundle.getString("allows.the.user.to.mirror.objects"));
        toolsMenu.add(mirrorSubmenu);

        JCheckBoxMenuItem mirrorFlip = new JCheckBoxMenuItem(resourceBundle.getString("automatically.flip.after.mirror.preserves.surface"), true);
        mirrorFlip.setMnemonic(KeyEvent.VK_A);

        createAndAddMenuItem(resourceBundle.getString("mirror.x"), mirrorSubmenu, KeyEvent.VK_X, e -> ModelEditActions.mirrorAxis(mainPanel, (byte) 0, mirrorFlip.isSelected()));

        createAndAddMenuItem(resourceBundle.getString("mirror.y"), mirrorSubmenu, KeyEvent.VK_Y, e -> ModelEditActions.mirrorAxis(mainPanel, (byte) 1, mirrorFlip.isSelected()));

        createAndAddMenuItem(resourceBundle.getString("mirror.z"), mirrorSubmenu, KeyEvent.VK_Z, e -> ModelEditActions.mirrorAxis(mainPanel, (byte) 2, mirrorFlip.isSelected()));

        mirrorSubmenu.add(new JSeparator());

        mirrorSubmenu.add(mirrorFlip);
    }

    private static void fillViewMenu(MainPanel mainPanel, JMenu viewMenu) {
        JCheckBoxMenuItem textureModels = new JCheckBoxMenuItem(resourceBundle.getString("texture.models"), true);
        textureModels.setMnemonic(KeyEvent.VK_T);
        textureModels.setSelected(mainPanel.prefs.textureModels());
        textureModels.addActionListener(e -> mainPanel.prefs.setTextureModels(textureModels.isSelected()));
        viewMenu.add(textureModels);

        JCheckBoxMenuItem showNormals = new JCheckBoxMenuItem(resourceBundle.getString("show.normals"), true);
        showNormals.setMnemonic(KeyEvent.VK_N);
        showNormals.setSelected(mainPanel.prefs.showNormals());
        showNormals.addActionListener(e -> mainPanel.prefs.setShowNormals(showNormals.isSelected()));
        viewMenu.add(showNormals);

        JCheckBoxMenuItem renderParticles = new JCheckBoxMenuItem(resourceBundle.getString("render.particles"), true);
        renderParticles.setMnemonic(KeyEvent.VK_P);
        renderParticles.setSelected(mainPanel.prefs.getRenderParticles());
        renderParticles.addActionListener(e -> mainPanel.prefs.setRenderParticles(renderParticles.isSelected()));
        viewMenu.add(renderParticles);

        JCheckBoxMenuItem showPerspectiveGrid = new JCheckBoxMenuItem(resourceBundle.getString("show.perspective.grid"), true);
        showPerspectiveGrid.setMnemonic(KeyEvent.VK_G);
        showPerspectiveGrid.setSelected(mainPanel.prefs.showPerspectiveGrid());
        showPerspectiveGrid.addActionListener(e -> mainPanel.prefs.setShowPerspectiveGrid(showPerspectiveGrid.isSelected()));
        viewMenu.add(showPerspectiveGrid);

        JMenuItem newDirectory = new JMenuItem(resourceBundle.getString("change.game.directory"));
        newDirectory.setAccelerator(KeyStroke.getKeyStroke("control shift D"));
        newDirectory.setToolTipText(resourceBundle.getString("change.game.directory.description"));
        newDirectory.setMnemonic(KeyEvent.VK_D);
        newDirectory.addActionListener(mainPanel);
//		viewMenu.add(newDirectory);

        viewMenu.add(new JSeparator());

        JCheckBoxMenuItem showVertexModifyControls = new JCheckBoxMenuItem(resourceBundle.getString("show.viewport.buttons"), true);
        // showVertexModifyControls.setMnemonic(KeyEvent.VK_V);
        showVertexModifyControls.addActionListener(e -> showVertexModifyControls(mainPanel.modelPanels, mainPanel.prefs, showVertexModifyControls));
        viewMenu.add(showVertexModifyControls);

        viewMenu.add(new JSeparator());

        JMenu viewMode = new JMenu(resourceBundle.getString("3d.view.mode"));
        viewMenu.add(viewMode);

        ButtonGroup viewModes = new ButtonGroup();

        JRadioButtonMenuItem wireframe = new JRadioButtonMenuItem(resourceBundle.getString("wireframe"));
        wireframe.addActionListener(e -> repaint(mainPanel, 0));
        wireframe.setSelected(mainPanel.prefs.getViewMode() == 0);
        viewMode.add(wireframe);
        viewModes.add(wireframe);

        JRadioButtonMenuItem solid = new JRadioButtonMenuItem(resourceBundle.getString("solid"));
        solid.addActionListener(e -> repaint(mainPanel, 1));
        solid.setSelected(mainPanel.prefs.getViewMode() == 1);
        viewMode.add(solid);
        viewModes.add(solid);

//        viewModes.setSelected(solid.getModel(), true);
    }

    private static void fillWindowsMenu(MainPanel mainPanel, JMenu windowMenu) {
        final JMenuItem resetViewButton = new JMenuItem(resourceBundle.getString("reset.layout"));
        resetViewButton.addActionListener(e -> resetView(mainPanel));
        windowMenu.add(resetViewButton);

        final JMenu viewsMenu = createMenu(resourceBundle.getString("views"), KeyEvent.VK_V);
        windowMenu.add(viewsMenu);

        final JMenuItem testItem = new JMenuItem(resourceBundle.getString("test"));
        testItem.addActionListener(new OpenViewAction(mainPanel.rootWindow, resourceBundle.getString("animation.preview"), () -> MenuBarActions.testItemResponse(mainPanel)));

//		viewsMenu.add(testItem);

        createAndAddMenuItem(resourceBundle.getString("animation.preview"), viewsMenu, KeyEvent.VK_A, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Animation Preview", mainPanel.previewView));
//        createAndAddMenuItem("Animation Preview", viewsMenu, KeyEvent.VK_A, new OpenViewAction(mainPanel.rootWindow, "Animation Preview", () -> mainPanel.previewView));

        createAndAddMenuItem(resourceBundle.getString("animation.controller"), viewsMenu, KeyEvent.VK_C, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Animation Controller", mainPanel.animationControllerView));

        createAndAddMenuItem(resourceBundle.getString("modeling"), viewsMenu, KeyEvent.VK_M, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Modeling", mainPanel.creatorView));

        createAndAddMenuItem(resourceBundle.getString("outliner"), viewsMenu, KeyEvent.VK_O, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Outliner", mainPanel.viewportControllerWindowView));

        createAndAddMenuItem(resourceBundle.getString("perspective"), viewsMenu, KeyEvent.VK_P, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Perspective", mainPanel.perspectiveView));

        createAndAddMenuItem(resourceBundle.getString("front"), viewsMenu, KeyEvent.VK_F, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Front", mainPanel.frontView));

        createAndAddMenuItem(resourceBundle.getString("side"), viewsMenu, KeyEvent.VK_S, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Side", mainPanel.leftView));

        createAndAddMenuItem(resourceBundle.getString("bottom"), viewsMenu, KeyEvent.VK_B, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Bottom", mainPanel.bottomView));

        createAndAddMenuItem(resourceBundle.getString("tools"), viewsMenu, KeyEvent.VK_T, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Tools", mainPanel.toolView));

        createAndAddMenuItem(resourceBundle.getString("contents"), viewsMenu, KeyEvent.VK_C, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Model", mainPanel.modelDataView));

        createAndAddMenuItem(resourceBundle.getString("footer"), viewsMenu, OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Footer", mainPanel.timeSliderView));

        createAndAddMenuItem(resourceBundle.getString("matrix.eater.script"), viewsMenu, KeyEvent.VK_H, KeyStroke.getKeyStroke("control P"), OpenViewAction.getOpenViewAction(mainPanel.rootWindow, "Matrix Eater Script", ScriptView.createHackerView(mainPanel)));

        final JMenu browsersMenu = createMenu(resourceBundle.getString("browsers"), KeyEvent.VK_B);
        windowMenu.add(browsersMenu);

        createAndAddMenuItem(resourceBundle.getString("data.browser"), browsersMenu, KeyEvent.VK_A, e -> MPQBrowserView.openMPQViewer(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("unit.browser1"), browsersMenu, KeyEvent.VK_U, e -> MenuBarActions.openUnitViewer(mainPanel));

//        createAndAddMenuItem("Doodad Browser", browsersMenu, KeyEvent.VK_D, getOpenDoodadViewerAction(mainPanel));
        createAndAddMenuItem(resourceBundle.getString("doodad.browser"), browsersMenu, KeyEvent.VK_D, e -> MPQBrowserView.OpenDoodadViewer(mainPanel));

        JMenuItem hiveViewer = new JMenuItem(resourceBundle.getString("hive.browser"));
        hiveViewer.setMnemonic(KeyEvent.VK_H);
        hiveViewer.addActionListener(e -> MenuBarActions.openHiveViewer(mainPanel));
//		browsersMenu.add(hiveViewer);

        windowMenu.addSeparator();
    }

    private static void fillAddMenu(final MainPanel mainPanel, JMenu addMenu) {
        JMenu addParticle = new JMenu(resourceBundle.getString("particle"));
        addParticle.setMnemonic(KeyEvent.VK_P);
        addMenu.add(addParticle);

        AddParticlePanel.addParticleButtons(mainPanel, addParticle);
        createAndAddMenuItem(resourceBundle.getString("empty.popcorn"), addParticle, KeyEvent.VK_O, e -> AddParticlePanel.addEmptyPopcorn(mainPanel));

        JMenu animationMenu = new JMenu(resourceBundle.getString("animation"));
        animationMenu.setMnemonic(KeyEvent.VK_A);
        addMenu.add(animationMenu);

        createAndAddMenuItem(resourceBundle.getString("empty"), animationMenu, KeyEvent.VK_F, e -> AddSingleAnimationActions.addEmptyAnimation(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("rising.falling.birth.death"), animationMenu, KeyEvent.VK_R, e -> AddBirthDeathSequences.riseFallBirthActionRes(mainPanel));

        JMenu singleAnimationMenu = new JMenu(resourceBundle.getString("single"));
        singleAnimationMenu.setMnemonic(KeyEvent.VK_S);
        animationMenu.add(singleAnimationMenu);

        createAndAddMenuItem(resourceBundle.getString("from.file"), singleAnimationMenu, KeyEvent.VK_F, e -> AddSingleAnimationActions.addAnimationFromFile(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("from.unit"), singleAnimationMenu, KeyEvent.VK_U, e -> AddSingleAnimationActions.addAnimationFromUnit(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("from.model"), singleAnimationMenu, KeyEvent.VK_M, e -> AddSingleAnimationActions.addAnimFromModel(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("from.object"), singleAnimationMenu, KeyEvent.VK_O, e -> AddSingleAnimationActions.addAnimationFromObject(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("material"), addMenu, KeyEvent.VK_M, e -> MenuBarActions.addNewMaterial(mainPanel));
//        JMenu addMaterial = new JMenu("Material");
//        addMaterial.setMnemonic(KeyEvent.VK_M);
//        addMenu.add(addMaterial);
    }

    private static void fillScriptsMenu(MainPanel mainPanel, JMenu scriptsMenu) {
        createAndAddMenuItem(resourceBundle.getString("oinkerwinkle.style.animtransfer"), scriptsMenu, KeyEvent.VK_P, KeyStroke.getKeyStroke("control shift S"), e -> importButtonS());

        FileDialog fileDialog = new FileDialog(mainPanel);

        JMenuItem mergeGeoset = new JMenuItem(resourceBundle.getString("oinkerwinkle.style.merge.geoset"));
        mergeGeoset.setMnemonic(KeyEvent.VK_M);
        mergeGeoset.setAccelerator(KeyStroke.getKeyStroke("control M"));
        mergeGeoset.addActionListener(e -> {
            try {
                ScriptActions.mergeGeosetActionRes(mainPanel);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        scriptsMenu.add(mergeGeoset);

        JMenuItem nullmodelButton = new JMenuItem(resourceBundle.getString("edit.delete.model.components"));
        nullmodelButton.setMnemonic(KeyEvent.VK_E);
        nullmodelButton.setAccelerator(KeyStroke.getKeyStroke("control E"));
        nullmodelButton.addActionListener(e -> ScriptActions.nullmodelButtonActionRes(mainPanel));
        scriptsMenu.add(nullmodelButton);

        createAndAddMenuItem(resourceBundle.getString("export.animated.to.static.mesh"), scriptsMenu,
                KeyEvent.VK_E, e -> ScriptActions.exportAnimatedToStaticMesh(mainPanel));

//        createAndAddMenuItem("Export Animated Frame PNG", scriptsMenu, KeyEvent.VK_F, e -> ScriptActions.exportAnimatedFramePNG(mainPanel));
        createAndAddMenuItem(resourceBundle.getString("export.animated.frame.png"), scriptsMenu,
                KeyEvent.VK_F, e -> fileDialog.exportAnimatedFramePNG());

        createAndAddMenuItem(resourceBundle.getString("copy.keyframes.between.animations"), scriptsMenu,
                KeyEvent.VK_K, e -> KeyframeCopyPanel.show(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("create.back2back.animation"), scriptsMenu,
                KeyEvent.VK_P, e -> ScriptActions.combineAnimations(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("change.animation.lengths.by.scaling"), scriptsMenu,
                KeyEvent.VK_A, e -> ScriptActions.scaleAnimations(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("assign.formatversion.800"), scriptsMenu,
                KeyEvent.VK_A, e -> mainPanel.currentMDL().setFormatVersion(800));

        createAndAddMenuItem(resourceBundle.getString("assign.formatversion.1000"), scriptsMenu,
                KeyEvent.VK_A, e -> mainPanel.currentMDL().setFormatVersion(1000));

        createAndAddMenuItem(resourceBundle.getString("sd.to.hd"), scriptsMenu,
                KeyEvent.VK_A, e -> ScriptActions.makeItHD(mainPanel.currentMDL()));

        createAndAddMenuItem(resourceBundle.getString("hd.to.sd"), scriptsMenu,
                KeyEvent.VK_A, e -> ScriptActions.convertToV800(1, mainPanel.currentMDL()));

        createAndAddMenuItem(resourceBundle.getString("remove.lods"), scriptsMenu,
                KeyEvent.VK_A, e -> ScriptActions.removeLoDs(mainPanel));

        createAndAddMenuItem(resourceBundle.getString("recalculate.tangents"), scriptsMenu,
                KeyEvent.VK_A, e -> MenuBarActions.recalculateTangents(mainPanel.currentMDL(), mainPanel));

        final JMenuItem jokebutton = new JMenuItem(resourceBundle.getString("load.retera.land"));
        jokebutton.setMnemonic(KeyEvent.VK_A);
        jokebutton.addActionListener(e -> ScriptActions.jokeButtonClickResponse(mainPanel));
//		scriptsMenu.add(jokebutton);
    }

    private static void fillAboutMenu(MainPanel mainPanel, JMenu aboutMenu) {
        createAndAddMenuItem(resourceBundle.getString("changelog"), aboutMenu, KeyEvent.VK_A, e -> MenuBarActions.createAndShowRtfPanel("docs/changelist.rtf", resourceBundle.getString("changelog")));

        createAndAddMenuItem(resourceBundle.getString("about"), aboutMenu, KeyEvent.VK_A, e -> MenuBarActions.createAndShowRtfPanel("docs/credits.rtf", resourceBundle.getString("about")));
    }

    private static void resetView(MainPanel mainPanel) {
        traverseAndReset(mainPanel.rootWindow);
        final TabWindow startupTabWindow = MainLayoutCreator.createMainLayout(mainPanel);
        startupTabWindow.setSelectedTab(0);
        mainPanel.rootWindow.setWindow(startupTabWindow);
        MPQBrowserView.setCurrentModel(mainPanel, mainPanel.currentModelPanel());
        mainPanel.rootWindow.revalidate();
        MainLayoutCreator.traverseAndFix(mainPanel.rootWindow);
    }

    private static void repaint(MainPanel mainPanel, int radioButton) {
        if (radioButton == 0) {
            mainPanel.prefs.setViewMode(0);
        } else if (radioButton == 1) {
            mainPanel.prefs.setViewMode(1);
        } else {
            mainPanel.prefs.setViewMode(-1);
        }
        mainPanel.repaint();
    }

//    private static void save(MainPanel mainPanel) {
//        if ((mainPanel.currentMDL() != null) && (mainPanel.currentMDL().getFile() != null)) {
//            MenuBarActions.onClickSave(mainPanel);
//        } else {
//            MenuBarActions.onClickSaveAs(mainPanel);
//        }
//    }

    private static void closeProgram(MainPanel mainPanel) {
        if (closeAll(mainPanel)) {
            MainFrame.frame.dispose();
        }
    }

    private static void copyCutPast(MainPanel mainPanel, TransferActionListener transferActionListener, ActionEvent e) {
        if (!mainPanel.animationModeState) {
            transferActionListener.actionPerformed(e);
        } else {
            if (e.getActionCommand().equals(TransferHandler.getCutAction().getValue(Action.NAME))) {
                mainPanel.timeSliderPanel.cut();
            } else if (e.getActionCommand().equals(TransferHandler.getCopyAction().getValue(Action.NAME))) {
                mainPanel.timeSliderPanel.copy();
            } else if (e.getActionCommand().equals(TransferHandler.getPasteAction().getValue(Action.NAME))) {
                mainPanel.timeSliderPanel.paste();
            }
        }
    }

    private static void sortBones(MainPanel mainPanel) {
        final EditableModel model = mainPanel.currentMDL();
        final List<IdObject> roots = new ArrayList<>();
        final List<IdObject> modelList = model.getIdObjects();
        for (final IdObject object : modelList) {
            if (object.getParent() == null) {
                roots.add(object);
            }
        }
        final Queue<IdObject> bfsQueue = new LinkedList<>(roots);
        final List<IdObject> result = new ArrayList<>();
        while (!bfsQueue.isEmpty()) {
            final IdObject nextItem = bfsQueue.poll();
            bfsQueue.addAll(nextItem.getChildrenNodes());
            result.add(nextItem);
        }
        for (final IdObject node : result) {
            model.remove(node);
        }
        mainPanel.modelStructureChangeListener.nodesRemoved(result);
        for (final IdObject node : result) {
            model.add(node);
        }
        mainPanel.modelStructureChangeListener.nodesAdded(result);
    }

    private static void minimizeGeoset(MainPanel mainPanel) {
        final int confirm = JOptionPane.showConfirmDialog(mainPanel,
                resourceBundle.getString("experimental.warning"),
                resourceBundle.getString("confirmation"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }

        mainPanel.currentMDL().doSavePreps();

        final Map<Geoset, Geoset> sourceToDestination = new HashMap<>();
        final List<Geoset> retainedGeosets = new ArrayList<>();
        for (final Geoset geoset : mainPanel.currentMDL().getGeosets()) {
            boolean alreadyRetained = false;
            for (final Geoset retainedGeoset : retainedGeosets) {
                if (retainedGeoset.getMaterial().equals(geoset.getMaterial())
                        && (retainedGeoset.getSelectionGroup() == geoset.getSelectionGroup())
                        && (retainedGeoset.getUnselectable() == geoset.getUnselectable())
                        && isGeosetAnimationsMergable(retainedGeoset.getGeosetAnim(), geoset.getGeosetAnim())) {
                    alreadyRetained = true;
                    for (final GeosetVertex gv : geoset.getVertices()) {
                        retainedGeoset.add(gv);
                    }
                    for (final Triangle t : geoset.getTriangles()) {
                        retainedGeoset.add(t);
                    }
                    break;
                }
            }
            if (!alreadyRetained) {
                retainedGeosets.add(geoset);
            }
        }
        final EditableModel currentMDL = mainPanel.currentMDL();
        final List<Geoset> geosets = currentMDL.getGeosets();
        final List<Geoset> geosetsRemoved = new ArrayList<>();
        final Iterator<Geoset> iterator = geosets.iterator();
        while (iterator.hasNext()) {
            final Geoset geoset = iterator.next();
            if (!retainedGeosets.contains(geoset)) {
                iterator.remove();
                final GeosetAnim geosetAnim = geoset.getGeosetAnim();
                if (geosetAnim != null) {
                    currentMDL.remove(geosetAnim);
                }
                geosetsRemoved.add(geoset);
            }
        }
        mainPanel.modelStructureChangeListener.geosetsRemoved(geosetsRemoved);
    }

    private static boolean isGeosetAnimationsMergable(final GeosetAnim first, final GeosetAnim second) {
        if ((first == null) && (second == null)) {
            return true;
        }
        if ((first == null) || (second == null)) {
            return false;
        }
        final AnimFlag<?> firstVisibilityFlag = first.getVisibilityFlag();
        final AnimFlag<?> secondVisibilityFlag = second.getVisibilityFlag();
        if ((firstVisibilityFlag == null) != (secondVisibilityFlag == null)) {
            return false;
        }
        if ((firstVisibilityFlag != null) && !firstVisibilityFlag.equals(secondVisibilityFlag)) {
            return false;
        }
        if (first.isDropShadow() != second.isDropShadow()) {
            return false;
        }
        if (Math.abs(first.getStaticAlpha() - second.getStaticAlpha()) > 0.001) {
            return false;
        }
        if ((first.getStaticColor() == null) != (second.getStaticColor() == null)) {
            return false;
        }
        if ((first.getStaticColor() != null) && !first.getStaticColor().equalLocs(second.getStaticColor())) {
            return false;
        }
        final AnimFlag<?> firstAnimatedColor = first.find("Color");
        final AnimFlag<?> secondAnimatedColor = second.find("Color");
        if ((firstAnimatedColor == null) != (secondAnimatedColor == null)) {
            return false;
        }
        return (firstAnimatedColor == null) || firstAnimatedColor.equals(secondAnimatedColor);
    }

    private static void createTeamColorMenuItems(MainPanel mainPanel) {
        for (int i = 0; i < 25; i++) {
            final String colorNumber = String.format("%2s", i).replace(' ', '0');
            try {
                final String colorName = WEString.getString("WESTRING_UNITCOLOR_" + colorNumber);
                final JMenuItem menuItem = new JMenuItem(colorName, new ImageIcon(BLPHandler.get()
                        .getGameTex("ReplaceableTextures\\TeamColor\\TeamColor" + colorNumber + ".blp")));
                mainPanel.teamColorMenu.add(menuItem);
                final int teamColorValueNumber = i;
                menuItem.addActionListener(e -> {
                    Material.teamColor = teamColorValueNumber;
                    final ModelPanel modelPanel = mainPanel.currentModelPanel();
                    if (modelPanel != null) {
                        modelPanel.getAnimationViewer().reloadAllTextures();
                        modelPanel.getPerspArea().reloadAllTextures();

                        ModelStructureChangeListenerImplementation.reloadComponentBrowser(mainPanel.geoControlModelData, modelPanel);
                    }
                    mainPanel.profile.getPreferences().setTeamColor(teamColorValueNumber);
                });
            } catch (final Exception ex) {
                // load failed
                break;
            }
        }
    }

    private static void removeMaterialDuplicates(MainPanel mainPanel) {
        EditableModel model = mainPanel.currentModelPanel().getModel();
        List<Material> materials = model.getMaterials();
        Map<Material, Material> sameMaterialMap = new HashMap<>();
        for (int i = 0; i < materials.size(); i++) {
            Material material1 = materials.get(i);
            for (int j = i + 1; j < materials.size(); j++) {
                Material material2 = materials.get(j);
                System.out.println(material1.getName() + " == " + material2.getName());
                if (material1.equals(material2)) {
                    if (!sameMaterialMap.containsKey(material2)) {
                        sameMaterialMap.put(material2, material1);
                    }
                }
            }
        }

        List<Geoset> geosets = model.getGeosets();
        for (Geoset geoset : geosets) {
            if (sameMaterialMap.containsKey(geoset.getMaterial())) {
                geoset.setMaterial(sameMaterialMap.get(geoset.getMaterial()));
            }
        }

        materials.removeAll(sameMaterialMap.keySet());
        mainPanel.modelStructureChangeListener.materialsListChanged();
    }

    static void traverseAndReset(final DockingWindow window) {
        final int childWindowCount = window.getChildWindowCount();
        for (int i = 0; i < childWindowCount; i++) {
            final DockingWindow childWindow = window.getChildWindow(i);
            traverseAndReset(childWindow);
            if (childWindow instanceof View) {
                final View view = (View) childWindow;
                view.getViewProperties().getViewTitleBarProperties().setVisible(true);
            }
        }
    }

    public static void updateRecent() {
        final List<String> recent = SaveProfile.get().getRecent();
        for (final RecentItem recentItem : recentItems) {
            recentMenu.remove(recentItem);
        }
        recentItems.clear();
        for (int i = 0; i < recent.size(); i++) {
            final String fp = recent.get(recent.size() - i - 1);
            if ((recentItems.size() <= i) || (!recentItems.get(i).filepath.equals(fp))) {
                // String[] bits = recent.get(i).split("/");

                final RecentItem item = new RecentItem(new File(fp).getName());
                item.filepath = fp;
                recentItems.add(item);
                item.addActionListener(e -> {

                    FileDialog.setCurrentFile(new File(item.filepath));
                    FileDialog.setCurrentPath(FileDialog.getCurrentFile().getParentFile());
                    // frontArea.clearGeosets();
                    // sideArea.clearGeosets();
                    // botArea.clearGeosets();
                    toolsMenu.getAccessibleContext().setAccessibleDescription(
                            resourceBundle.getString("model.display.description"));
                    toolsMenu.setEnabled(true);
                    SaveProfile.get().addRecent(FileDialog.getCurrentFile().getPath());
                    updateRecent();
                    MPQBrowserView.loadFile(mainPanel, FileDialog.getCurrentFile());
                });
                recentMenu.add(item, recentMenu.getItemCount() - 2);
            }
        }
    }

    public static boolean closeAll(MainPanel mainPanel) {
        boolean success = true;
        final Iterator<ModelPanel> iterator = mainPanel.modelPanels.iterator();
        boolean closedCurrentPanel = false;
        ModelPanel lastUnclosedModelPanel = null;
        while (iterator.hasNext()) {
            final ModelPanel panel = iterator.next();
            if (success = panel.close(mainPanel)) {
//                mainPanel.windowMenu.remove(panel.getMenuItem());
                windowMenu.remove(panel.getMenuItem());
                iterator.remove();
                if (panel == mainPanel.currentModelPanel) {
                    closedCurrentPanel = true;
                }
            } else {
                lastUnclosedModelPanel = panel;
                break;
            }
        }
        if (closedCurrentPanel) {
            MPQBrowserView.setCurrentModel(mainPanel, lastUnclosedModelPanel);
        }
        return success;
    }

    static void traverseAndReloadData(final DockingWindow window) {
        final int childWindowCount = window.getChildWindowCount();
        for (int i = 0; i < childWindowCount; i++) {
            final DockingWindow childWindow = window.getChildWindow(i);
            traverseAndReloadData(childWindow);
            if (childWindow instanceof View) {
                final View view = (View) childWindow;
                final Component component = view.getComponent();
                if (component instanceof JScrollPane) {
                    final JScrollPane pane = (JScrollPane) component;
                    final Component viewportView = pane.getViewport().getView();
                    if (viewportView instanceof UnitEditorTree) {
                        final UnitEditorTree unitEditorTree = (UnitEditorTree) viewportView;
                        final MutableObjectData.WorldEditorDataType dataType = unitEditorTree.getDataType();
                        if (dataType == MutableObjectData.WorldEditorDataType.UNITS) {
                            System.out.println("saw unit tree");
                            unitEditorTree.setUnitDataAndReloadVerySlowly(MainLayoutCreator.getUnitData());
                        } else if (dataType == MutableObjectData.WorldEditorDataType.DOODADS) {
                            System.out.println("saw doodad tree");
                            unitEditorTree.setUnitDataAndReloadVerySlowly(MenuBarActions.getDoodadData());
                        }
                    }
                } else if (component instanceof MPQBrowser) {
                    System.out.println("saw mpq tree");
                    final MPQBrowser comp = (MPQBrowser) component;
                    comp.refreshTree();
                }
            }
        }
    }

    static void showVertexModifyControls(List<ModelPanel> modelPanels, ProgramPreferences prefs, JCheckBoxMenuItem showVertexModifyControls) {
        final boolean selected = showVertexModifyControls.isSelected();
        prefs.setShowVertexModifierControls(selected);
        // SaveProfile.get().setShowViewportButtons(selected);
        for (final ModelPanel panel : modelPanels) {
            panel.getFrontArea().setControlsVisible(selected);
            panel.getBotArea().setControlsVisible(selected);
            panel.getSideArea().setControlsVisible(selected);
            final UVPanel uvPanel = panel.getEditUVPanel();
            if (uvPanel != null) {
                uvPanel.setControlsVisible(selected);
            }
        }
    }

    static void importButtonS() {
        final JFrame frame = new JFrame(resourceBundle.getString("animation.transferer"));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new AnimationTransfer(frame));
        frame.setIconImage(AnimIcon.getImage());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static class RecentItem extends JMenuItem {
        public RecentItem(final String what) {
            super(what);
        }

        String filepath;
    }

    static class UndoMenuItem extends JMenuItem {
        private final MainPanel mainPanel;

        public UndoMenuItem(MainPanel mainPanel, final String text) {
            super(text);
            this.mainPanel = mainPanel;
        }

        @Override
        public String getText() {
            if (funcEnabled()) {
                return MessageFormat.format(resourceBundle.getString("undo.0"), mainPanel.currentModelPanel().getUndoManager().getUndoText());// +"
                // Ctrl+Z";
            } else {
                return resourceBundle.getString("cannot.undo");// +" Ctrl+Z";
            }
        }

        public boolean funcEnabled() {
            try {
                return !mainPanel.currentModelPanel().getUndoManager().isUndoListEmpty();
            } catch (final NullPointerException e) {
                return false;
            }
        }
    }

    static class RedoMenuItem extends JMenuItem {
        private final MainPanel mainPanel;

        public RedoMenuItem(MainPanel mainPanel, final String text) {
            super(text);
            this.mainPanel = mainPanel;
        }

        @Override
        public String getText() {
            if (funcEnabled()) {
                return MessageFormat.format(resourceBundle.getString("redo.0"), mainPanel.currentModelPanel().getUndoManager().getRedoText());// +"
                // Ctrl+Y";
            } else {
                return resourceBundle.getString("cannot.redo");// +" Ctrl+Y";
            }
        }

        public boolean funcEnabled() {
            try {
                return !mainPanel.currentModelPanel().getUndoManager().isRedoListEmpty();
            } catch (final NullPointerException e) {
                return false;
            }
        }
    }

}
