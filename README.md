## Usage
### Basic

```java
View yourView = findViewById(R.id.your_view);

new Popup.Builder(this)
    .anchorView(yourView)
    .text("Hello world!")
    .gravity(Gravity.END)
    .animated(true)
    .transparentOverlay(false)
    .build()
    .show();
```
