#!/bin/sh
# Assumes running on Ubuntu.

set -e -u

TOPDIR=`cd $(dirname $0); pwd`
TMPDIR=$TOPDIR/tmp
FONTDIR=$TOPDIR/app/src/main/assets/fonts/

rm -Rf $TMPDIR
mkdir -p $TMPDIR

# Install python fontforge bindings for use by powerline font patcher:
if [ $(dpkg-query -W -f='${Status}' python-fontforge 2>/dev/null | grep -c "ok installed") -eq 0 ]; then
	sudo apt install python-fontforge
fi

FONTPATCHER_DIR=$HOME/src/fontpatcher
if [ -d $FONTPATCHER_DIR ]; then
	(cd $FONTPATCHER_DIR; git pull --rebase)
else
	mkdir -p $HOME/src
	git clone https://github.com/powerline/fontpatcher.git $FONTPATCHER_DIR
	cd $FONTPATCHER_DIR
	./setup.py build
fi
FONTPATCHER=$FONTPATCHER_DIR/scripts/powerline-fontpatcher

# Setup Courier-Prime.ttf - http://quoteunquoteapps.com/courierprime
cd $TMPDIR
curl -L -O http://quoteunquoteapps.com/downloads/courier-prime.zip
unzip -o -q courier-prime.zip
$FONTPATCHER "Courier Prime/Courier Prime.ttf"
mv "CourierPrime for Powerline.ttf" $FONTDIR/Courier-Prime.ttf

# Setup GNU-FreeFont.ttf - https://www.gnu.org/software/freefont/
cd $TMPDIR
curl -L -O https://ftp.gnu.org/gnu/freefont/freefont-otf-20120503.tar.gz
tar xf freefont-otf-20120503.tar.gz
$FONTPATCHER freefont-20120503/FreeMono.otf
mv "FreeMono for Powerline.otf" $FONTDIR/GNU-FreeFont.ttf
