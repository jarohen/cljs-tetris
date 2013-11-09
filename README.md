# Tetris (in ClojureScript)

This is a Tetris clone written using ClojureScript and core.async.

It was originally written for a [Likely](http://likely.co) hack-day,
to investigate using core.async within a ClojureScript app, and to
experiment with design patterns in ClojureScript.

## Getting it running:

After you've forked/cloned this repo, you can start Tetris by running:

    lein start
	
This will compile the CLJS files (once) and start a lightweight web server on
http://localhost:3000
	
If you want to hack on Tetris, you can use:

    lein dev

This sets up a `lein cljsbuild auto` process, to automatically
re-compile the CLJS files, the web server, and an nREPL server for the
web-app.

## Design/development rationale

*When I get a moment, I'll write up a longer piece about the various
 design patterns that I've used here - watch this space!*
 
*In the meantime - a lot of the inspiration for the design came from
 David Nolen's [series of blogs](https://swannodette.github.io) on
 ClojureScript and core.async, and conversations with
 [Simon Hicks](https://github.com/simonhicks) - thank you to you
 both!*

### Setting up a CLJS project

The first concept I found myself repeating was a general project
structure, which I've released in the form of a Lein template called
SPLAT (Single Page Lightweight App Template). This creates a small
Compojure handler and a CLJS file that puts 'Hello World from
ClojureScript' in the browser. To start:

* lein new splat <your-app>
* cd <your-app>
* lein dev

`lein dev` is an alias included in the template that starts up an
HTTPkit server and a cljsbuild-auto process.

### Browser REPL

I really liked Chas Emerick's Austin REPL, but found myself frequently
heading back to his (albeit very good) browser REPL example to grab
the startup code. SPLAT therefore includes an easy way to get started
using [lein-frodo](https://github.com/james-henderson/lein-frodo) (which
is itself a thin layer built atop Austin), so my process is now as
follows:

* lein dev (from above)
* Connect to Clojure REPL (for me - using Emacs/cider)
* Run `(frodo/cljs-repl)` to turn REPL into a CLJS REPL. (no requires required)
* Refresh browser to connect
* Test, using either basic CLJS arithmetic or `(js/alert "Hello world!")`

We're using both SPLAT and Frodo in production at work and both are
now reasonably stable (although inevitable bug reports/fixes always
appreciated!)

### CLJS patterns

The main 'pattern' (if you can call it that!) was essentially the
age-old idea of splitting out rendering logic from business
logic. (Grandmothers and eggs spring to mind!) In ClojureScript,
though, this took on a specific form - what I've come to call
*widgets* and *model*.

The model side of the system takes in a core.async channel of commands
(expressed in high-level business-logic terms) and updates an
atom. The model knows nothing about where it's being rendered - its
sole purpose is to manipulate vanilla Clojure data structures using
vanilla Clojure functions (and can hence be tested by a vanilla
Clojure REPL!).

The widget side of the system watches the aforementioned atom for any
changes, re-renders the DOM as appropriate and translates relevant DOM
events into business commands. Again, the widget has no idea what is
manipulating the atom or actioning the commands, so it is easy to test
by manipulating the atom through a REPL, and simply logging the
commands received.

For the game itself, the main widget is in
[src/cljs/clojurex_demo/cljs/board_widget.cljs](https://github.com/james-henderson/cljs-tetris/blob/master/src/cljs/clojurex_demo/cljs/board_widget.cljs)
and the main model manipulation is in [src/cljs/clojurex_demo/cljs/game_model.cljs](https://github.com/james-henderson/cljs-tetris/blob/master/src/cljs/clojurex_demo/cljs/game_model.cljs).

For the WebSocket side, I used
[Chord](https://github.com/james-henderson/chord) - a library that
makes WebSockets appear to be core.async channels. I found that this
fit in well with the existing command channels, and I could re-use the
same patterns that I used for browser events for WebSocket
communication. The main WebSocket code is in
[src/cljs/clojurex_demo/cljs/multiplayer_model.cljs](https://github.com/james-henderson/cljs-tetris/blob/master/src/cljs/clojurex_demo/cljs/multiplayer_model.cljs)
(client-side) and
[src/clojure/clojurex_demo/multiplayer.clj](https://github.com/james-henderson/cljs-tetris/blob/master/src/clojure/clojurex_demo/multiplayer.clj)
(server-side)

## Health Warning: 

This clone is as addictive as any other Tetris clone. If you do suffer
from symptoms of addiction, please stop playing immediately and seek
medical advice/alternative forms of entertainment. ;)

## Licence

This code is meant to be an educational code base - at the very least
for myself. If it helps others as well - that's great! Feel free to
hack about with it!

I'd be grateful if you'd let me know of any enhancements/suggestions
etc, either by usual GitHub means or by tweeting
[@jarohen](https://twitter.com/jarohen). Thanks!

&copy; James Henderson 2013, all rights reserved.

