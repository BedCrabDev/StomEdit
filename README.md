# StomEdit

StomEdit is a FAWE-like implementation of WorldEdit inside of Minestom.

## Setup

Add the project as a dependency:

```gradle
// for the examples below, relace TAG with a tag from
// https://jitpack.io/#BedCrabDev/StomEdit
// or, use -SNAPSHOT for the latest builds

// Minestom
implementation("com.github.BedCrabDev:StomEdit:TAG") {
  exclude(group = "dev.hollowcube", module = "minestom-ce")
}

// minestom-ce
implementation("com.github.BedCrabDev:StomEdit:TAG")
```

(make sure you have also added `jitpack.io` as a repo)

Then, insert the code above after `MinestomServer.init();` and before starting the server:

```java
EventNode<Event> parentEventNode = ...; // you can use the global event node here if you want
StomEdit stomEdit = new StomEdit();

// enable everything
stomEdit.enable();

// or
stomEdit.bltoolEnable();
stomEdit.commandsEnable();
```

StomEdit is not available as an extension.

## Commands

**Block Tool**  
`//bltool`: Obtain the block tool.  
`//bltoolmode <mode>`: Set the mode of the block tool. Available modes are: BRUSH, MODIFY, SELECT

**Block Manipulation**  
`//set`: Fills the selected region with a block.  
`//toolshape <shape>`: Changes the shape of the selection and brush.

**Other**  
`//debug`: Is this supposed to be in production?
