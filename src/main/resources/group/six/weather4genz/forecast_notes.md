## Adding the forecast menu to homepage_3:

- The homepage's (homepage_3.fxml) main content consists of a StackPane. Initially in front is the homepage, with current
temperature and hourly forecast etc.
- The StackPane also contains a transparent ScrollPane, initially behind the homepage, with id "forecast_scrollpane".
- This ScrollPane contains an AnchorPain, which is initially off the screen, with id "forecast_anchor".
- Things that need to be done to add the forecast page:

1. Attach the root forecast node to the anchor.
2. Ensure that when the forecast menu is not in use, that the scrollpane is the back layer (otherwise clicks are not
registered on the homepage). For this you can use the `.toBack ()` and `.toFront ()` methods on the scrollpane.
3. Have the scrollpane come to the front layer when the button ("forecast_open_button") is pressed, and be automatically
scrolled up.
4. When the window is resized, ensure that the anchor is pushed off the bottom of the screen accordingly.
5. Recognise when the scrollpane is scrolled closed, and send it to the back of the stack.