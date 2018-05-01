package sepr.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import sepr.game.saveandload.GameState;
import sepr.game.saveandload.SaveLoadManager;

import java.util.Arrays;
import java.util.List;

/**
 * NEW CLASS - ASSESSMENT 4
 * Save/Load screen to allow the user to save and load the game
 */
public class LoadScreen implements Screen{

    private Main main;
    private Stage stage;
    private Table table;
    private SaveLoadManager saveLoadManager;

    private EntryPoint entryPoint;

    private Texture selectSaveBox;

    private int saveID = -1;

    private Stage loadingWidgetStage;
    private boolean isLoading;
    private boolean loadingWidgetDrawn;

    /**
     *
     * @param main for changing to different screens
     * @param entryPoint for reference as to where screen was entered from
     */
    public LoadScreen (final Main main, EntryPoint entryPoint, SaveLoadManager saveLoadManager) {
        this.main = main;
        this.entryPoint = entryPoint;
        this.saveLoadManager = saveLoadManager;

        if (entryPoint == EntryPoint.MENU_SCREEN) {
            this.stage = new Stage() {
                @Override
                public boolean keyUp(int keyCode) {
                    if (keyCode == Input.Keys.ESCAPE) { // change back to the menu screen if the player presses esc
                        main.setMenuScreen();
                    }
                    return super.keyUp(keyCode);
                }
            };
        }
        else {
            this.stage = new Stage() {
                @Override
                public boolean keyUp(int keyCode) {
                    if (keyCode == Input.Keys.ESCAPE) { // change back to the game screen if the player presses esc
                        main.returnGameScreen();
                    }
                    return super.keyUp(keyCode);
                }
            };
        }

        this.selectSaveBox = new Texture("uiComponents/selectSaveBttn.png");

        this.loadingWidgetStage = new Stage();
        this.isLoading = false;
        this.loadingWidgetDrawn = false;

        this.stage.setViewport(new ScreenViewport());
        this.table = new Table();
        this.stage.addActor(table);
        this.table.setFillParent(true);
        this.table.setDebug(false);
        this.setupUi();

    }

    /**
     * sets up loading widget to be shown when game starts
     */
    @SuppressWarnings("Duplicates")
    private void showLoadingWidget() {
        isLoading = true;
        Table table = new Table();
        table.setDebug(false);
        table.setFillParent(true);
        table.add(new Image(new Texture("uiComponents/loadingBox.png")));
        loadingWidgetStage.addActor(table);
    }

    /**
     * sets up table displaying saves
     *
     * @return table displaying saves
     */
    private Table setupSelectSaveTable() {
        Table saveTable = new Table();
        saveTable.setDebug(false);

        Label.LabelStyle smallStyle = new Label.LabelStyle();
        smallStyle.font = WidgetFactory.getFontBig();

        Label.LabelStyle bigStyle = new Label.LabelStyle();
        bigStyle.font = WidgetFactory.getFontSmall();

        final Table[] saveTables = new Table[] {new Table(), new Table(), new Table(), new Table()};
        final List<Boolean> clickedTables = Arrays.asList(new Boolean[]{false,false,false,false});
        for (int i = 0; i < saveTables.length; i++) {
            GameState loadedState = saveLoadManager.getLoadedStates()[i];
            if ((loadedState == null) && (this.entryPoint == EntryPoint.MENU_SCREEN)) {
                continue;
            }
            final int thisTableNo = i;
            final Table t = saveTables[i];
            t.setDebug(false);
            t.setTouchable(Touchable.enabled);
            t.setBackground(new TextureRegionDrawable(new TextureRegion(selectSaveBox, 0,0, 1240, 208)));
            t.addListener(new ClickListener() {
                private Table[] allTables = saveTables;
                private List<Boolean> clickedSave = clickedTables;
                private int tableNo = thisTableNo;

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    for(int i = 0; i < clickedSave.size(); i++) {
                        clickedSave.set(i, false);
                        allTables[i].setBackground(new TextureRegionDrawable(new TextureRegion(selectSaveBox, 0, 0, 1240, 208)));
                    }
                    clickedSave.set(tableNo, true);
                    allTables[tableNo].setBackground(new TextureRegionDrawable(new TextureRegion(selectSaveBox, 0,208, 1240, 208)));
                    saveID = tableNo;
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                    super.enter(event, x, y, pointer, fromActor);
                    t.setBackground(new TextureRegionDrawable(new TextureRegion(selectSaveBox, 0,208, 1240, 208)));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                    super.exit(event, x, y, pointer, toActor);
                    if (!clickedSave.get(tableNo)) {
                        t.setBackground(new TextureRegionDrawable(new TextureRegion(selectSaveBox, 0, 0, 1240, 208)));
                    }
                }
            });

            if (saveLoadManager.getLoadedStates()[i] != null) {
                Integer[] keys = loadedState.players.keySet().toArray(new Integer[loadedState.players.size()]);
                Player player1 = loadedState.players.get(keys[0]);
                t.row().left();
                t.add(new Image(WidgetFactory.genCollegeLogoDrawable(player1.getCollegeName()))).width(150).height(120).padRight(10).padLeft(10);
                for (int j = 1; j < loadedState.players.size(); j++) {
                    t.add(new Label("V", smallStyle));
                    t.add(new Image(WidgetFactory.genCollegeLogoDrawable(loadedState.players.get(keys[j]).getCollegeName()))).width(150).height(120).padRight(10).padLeft(10);
                }
                t.row().left();
                t.add(new Label(player1.getPlayerName(), bigStyle)).center();
                for (int j = 1; j < loadedState.players.size(); j++) {
                    t.add();
                    t.add(new Label(loadedState.players.get(keys[j]).getPlayerName(), bigStyle)).center();
                }

            } else {
            t.row().center();
            t.add(new Label("EMPTY SAVE SLOT", smallStyle));
            }
            stage.addActor(t);

            saveTable.row();
            saveTable.add(t).height(200).padBottom(20);
        }

        return saveTable;

    }

    /**
     * sets up the UI for the load screen
     */
    private void setupUi() {

        // add the menu background
        table.background(new TextureRegionDrawable(new TextureRegion(new Texture("uiComponents/menuBackground.png"))));

        if (entryPoint == EntryPoint.MENU_SCREEN) {
            table.center();
            table.add(WidgetFactory.genMenusTopBar("LOAD GAME")).colspan(2);
        }
        else {
            table.center();
            table.add(WidgetFactory.genMenusTopBar("SAVE GAME")).colspan(2);
        }

        table.row().padTop(60);
        table.add(setupSelectSaveTable());

        final TextButton saveButton = WidgetFactory.genStartGameButton();
        saveButton.setText("SAVE");
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (saveID == -1) {
                    DialogFactory.basicDialogBox("Save Unsuccessful", "A save slot must be selected first.", getStage());
                } else {
                    boolean saved = saveLoadManager.saveByID(saveID);
                    saveLoadManager.saveToFile();
                    if (saved) {
                        LoadScreen save = main.getSaveScreen();
                        DialogFactory.basicDialogBox("Save Successful", "The game has been successfully saved.", save.getStage());
                    }
                }}});

        TextButton loadButton = WidgetFactory.genStartGameButton();
        loadButton.setText("LOAD");
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (saveID == -1) {
                    DialogFactory.basicDialogBox("Load Failure", "No save has been selected", stage);
                } else {
                    try {
                        if (saveLoadManager.getLoadedStates()[saveID] != null) {
                            showLoadingWidget();
                        }
                    } catch (Exception e) {
                        DialogFactory.basicDialogBox("Load Failure", "There is no save game to load in that slot.", stage);
                    }
                }
            }
        });

        Table subTable = new Table();
        subTable.setDebug(false);

        if (entryPoint != EntryPoint.MENU_SCREEN) {
            subTable.row();
            subTable.add(saveButton).fill().height(60).width(300).padBottom(20);
        }

        subTable.row();
        subTable.add(loadButton).fill().height(60).width(300);

        table.add(subTable).expandX();

        table.row();
        table.add().expand();

        if (entryPoint == EntryPoint.MENU_SCREEN) {
            table.row();
            table.add(WidgetFactory.genBottomBar("MAIN MENU", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    main.setMenuScreen();}

            })).colspan(2);
        }
        else {
            table.row();
            table.add(WidgetFactory.genBottomBar("RETURN", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    main.returnGameScreen();}

            })).colspan(2);
        }

    }

    /**
     *
     * @return the stage of the current screen.
     */
    private Stage getStage() {
        return this.stage;
    }

    /**
     * change the input processing to be handled by this screen's stage
     */
    @Override
    public void show() {
        isLoading = false;
        loadingWidgetDrawn = false;
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (loadingWidgetDrawn) {
            try {
                saveLoadManager.loadSaveByID(saveID);
                main.returnGameScreen();
            } catch (Exception e) {
                e.printStackTrace();
                DialogFactory.basicDialogBox("Load Failure","Load has been Unsuccessful", stage);
            }
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.act(Gdx.graphics.getDeltaTime());
        this.stage.draw();
        if (isLoading) {
            loadingWidgetStage.draw();
            loadingWidgetDrawn = true;
        }
    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height, true);
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }



}
