# StomEdit

StomEdit is a FAWE-like implementation of WorldEdit inside of Minestom.

## Setup

Add the project as a dependency:

```gradle
// for the examples below, replace TAG with a tag from
// https://jitpack.io/#BedCrabDev/StomEdit
// or, use -SNAPSHOT for the latest builds.

// Minestom
implementation("com.github.BedCrabDev:StomEdit:TAG") {
  exclude(group = "dev.hollowcube", module = "minestom-ce")
}

// minestom-ce
implementation("com.github.BedCrabDev:StomEdit:TAG")
```

(make sure you have also added `jitpack.io` as a repo)

How to use (insert in server initialization code):

```java
EventNode<Event> rootNode = ...; // you can use the global event node here if you want 
CommandManager commandManager = MinecraftServer.getCommandManager();
StomEdit stomEdit = new StomEdit(null);

// enable everything
stomEdit.enable(rootNode, commandManager);

// or
stomEdit.bltoolEnable();
stomEdit.commandsEnable(commandManager);
```

StomEdit is not available as an extension.

## Commands

**Block Tool**  
`//bltool`: Obtain the block tool.  
`//bltoolmode <mode>`: Set the mode of the block tool. Available modes are: BRUSH, MODIFY, SELECT
`//toolshape <shape>`: Changes the shape of the selection and brush.

**Block Manipulation**  
`//set`: Fills the selected region with a block.  

**Other**  
`//debug`: Is this supposed to be in production?
