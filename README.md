# Termux:Styling

[![Build status](https://github.com/termux/termux-styling/workflows/Build/badge.svg)](https://github.com/termux/termux-styling/actions)
[![Join the chat at https://gitter.im/termux/termux](https://badges.gitter.im/termux/termux.svg)](https://gitter.im/termux/termux)

A [Termux](https://termux.org) add-on app to customize the terminal font and
color theme.

When developing (or packaging), note that this app needs to be signed with the
same key as the main Termux app in order to have the permission to modify the
required font or color files.

## Installation

Termux:Styling application can be obtained from [F-Droid](https://f-droid.org/en/packages/com.termux.styling/).

Additionally we provide per-commit debug builds for those who want to try
out the latest features or test their pull request. This build can be obtained
from one of the workflow runs listed on [Github Actions](https://github.com/termux/termux-styling/actions)
page.

Signature keys of all offered builds are different. Before you switch the
installation source, you will have to uninstall the Termux application and
all currently installed plugins. Check https://github.com/termux/termux-app#Installation for more info.

## How to use

1. When inside Termux, long press anywhere on the terminal.
2. Select `More...` in the resulting dialog.
3. Select `Style` in the next dialog.
4. Click either `CHOOSE COLOR` or `CHOOSE FONT` depending on what you want to customize.
