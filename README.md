# StomEdit

StomEdit is a FAWE-like implementation of WorldEdit inside of Minestom.

**Warning! ⚠ This project has only been tested with `minestom-ce`. It may not work well with base Minestom.**

## Setup

```java
EventNode<Event> parentEventNode = ...; // you can use the global event node here if you want
StomEdit stomEdit = new StomEdit();

// enable everything
stomEdit.enable();

// or
stomEdit.bltoolEnable();
stomEdit.commandsEnable();
```

StomEdit is not available as an extension. Insert the code above after `MinestomServer.init();` and before starting the server.

## Commands

**Block Tool**  
`//bltool`: Obtain the block tool.  
`//bltoolmode <mode>`: Set the mode of the block tool. Available modes are: BRUSH, MODIFY, SELECT

**Block Manipulation**  
`//set`: Fills the selected region with a block.  
`//toolshape <shape>`: Changes the shape of the selection and brush.

**Other**  
`//debug`: Is this supposed to be in production?
