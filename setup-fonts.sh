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

# Setup Roboto.ttf - https://github.com/google/fonts/tree/master/apache/robotomono
cd $TMPDIR
# Powerline patching does not work for this font - grab an already patched font file.
curl -L -o $FONTDIR/Roboto.ttf https://github.com/powerline/fonts/raw/master/RobotoMono/Roboto%20Mono%20for%20Powerline.ttf
# curl -L -O https://github.com/google/fonts/raw/master/apache/robotomono/RobotoMono-Regular.ttf
# $FONTPATCHER RobotoMono-Regular.ttf
# mv "RobotoMono Regular for Powerline.ttf" $FONTDIR/Roboto.ttf

# Setup Meslo.ttf - https://github.com/andreberg/Meslo-Font
cd $TMPDIR
curl -L -O https://github.com/andreberg/Meslo-Font/raw/master/dist/v1.2.1/Meslo%20LG%20v1.2.1.zip
unzip -q Meslo%20LG%20v1.2.1.zip
$FONTPATCHER "Meslo LG v1.2.1/MesloLGL-Regular.ttf"
mv "Meslo LG L Regular for Powerline.ttf" $FONTDIR/Meslo.ttf

# Setup Go-Mono.ttf - https://go.googlesource.com/image/+/master/font/gofont/ttfs
cd $TMPDIR
curl -L -O https://go.googlesource.com/image/+archive/master/font/gofont/ttfs.tar.gz
tar xf ttfs.tar.gz
$FONTPATCHER Go-Mono.ttf
mv "Go Mono for Powerline.ttf" $FONTDIR/Go.ttf

# Setup Anonymous-Pro.ttf - http://www.marksimonson.com/fonts/view/anonymous-pro
cd $TMPDIR
curl -L -O http://www.marksimonson.com/assets/content/fonts/AnonymousProMinus-1.003.zip
unzip -o -q AnonymousProMinus-1.003.zip
$FONTPATCHER "AnonymousProMinus-1.003/Anonymous Pro Minus.ttf"
mv "Anonymous Pro Minus for Powerline.ttf" $FONTDIR/Anonymous-Pro.ttf

# Setup Courier-Prime.ttf - http://quoteunquoteapps.com/courierprime
cd $TMPDIR
curl -L -O http://quoteunquoteapps.com/downloads/courier-prime.zip
unzip -o -q courier-prime.zip
$FONTPATCHER "Courier Prime/Courier Prime.ttf"
mv "CourierPrime for Powerline.ttf" $FONTDIR/Courier-Prime.ttf

# Setup DejaVu.ttf - http://dejavu-fonts.org/wiki/Main_Page
cd $TMPDIR
curl -L -O http://downloads.sourceforge.net/project/dejavu/dejavu/2.37/dejavu-fonts-ttf-2.37.zip
unzip -o -q dejavu-fonts-ttf-2.37.zip
$FONTPATCHER dejavu-fonts-ttf-2.37/ttf/DejaVuSansMono.ttf
mv "DejaVu Sans Mono for Powerline.ttf" $FONTDIR/DejaVu.ttf

# Setup Fantasque.ttf - https://github.com/belluzj/fantasque-sans
cd $TMPDIR
curl -L -O https://github.com/belluzj/fantasque-sans/releases/download/v1.7.1/FantasqueSansMono.zip
unzip -o -q FantasqueSansMono.zip
# Powerline glyphs already integrated in this font.
mv FantasqueSansMono-Regular.ttf $FONTDIR/Fantasque.ttf

# Setup Fira.ttf - https://github.com/mozilla/Fira
cd $TMPDIR
curl -L -O https://github.com/mozilla/Fira/raw/4.202/otf/FiraMono-Regular.otf
$FONTPATCHER FiraMono-Regular.otf
mv "Fira Mono Regular for Powerline.otf" $FONTDIR/Fira.ttf

# Setup FiraCode.ttf - https://github.com/tonsky/FiraCode
# Powerline glyphs already integrated in this font.
curl -L -o $FONTDIR/FiraCode.ttf https://github.com/tonsky/FiraCode/raw/1.204/distr/otf/FiraCode-Regular.otf

# Setup GNU-FreeFont.ttf - https://www.gnu.org/software/freefont/
cd $TMPDIR
curl -L -O https://ftp.gnu.org/gnu/freefont/freefont-otf-20120503.tar.gz
tar xf freefont-otf-20120503.tar.gz
$FONTPATCHER freefont-20120503/FreeMono.otf
mv "FreeMono for Powerline.otf" $FONTDIR/GNU-FreeFont.ttf

# Setup Hack.ttf - https://github.com/chrissimpkins/Hack
# Powerline glyphs already integrated in this font.
curl -L -o $FONTDIR/Hack.ttf https://github.com/chrissimpkins/Hack/raw/v2.020/build/otf/Hack-Regular.otf

# Setup Hermit.ttf - https://pcaro.es/p/hermit/
cd $TMPDIR
curl -L -O https://pcaro.es/d/otf-hermit-1.21.tar.gz
tar xf otf-hermit-1.21.tar.gz
# Powerline glyphs already integrated in this font.
cp Hermit-medium.otf $FONTDIR/Hermit.ttf

# Setup Inconsolata.ttf - http://levien.com/type/myfonts/inconsolata.html
# Powerline patching does not work for this font - grab an already patched font file.
curl -L -o $FONTDIR/Inconsolata.ttf https://github.com/powerline/fonts/raw/master/Inconsolata/Inconsolata%20for%20Powerline.otf
# cd $TMPDIR
# curl -L -O http://www.levien.com/type/myfonts/Inconsolata.otf
# $FONTPATCHER Inconsolata.otf
# mv "Inconsolata for Powerline.otf" $FONTDIR/Inconsolata.ttf

# Setup LiberationMono.ttf - https://pagure.io/liberation-fonts
cd $TMPDIR
curl -L -O https://releases.pagure.org/liberation-fonts/liberation-fonts-ttf-2.00.1.tar.gz
tar xf liberation-fonts-ttf-2.00.1.tar.gz
$FONTPATCHER liberation-fonts-ttf-2.00.1/LiberationMono-Regular.ttf
mv "Liberation Mono for Powerline.ttf" $FONTDIR/LibrationMono.ttf

# Setup Monoid.ttf - https://github.com/larsenwork/monoid/
cd $TMPDIR
curl -L -O https://cdn.rawgit.com/larsenwork/monoid/2db2d289f4e61010dd3f44e09918d9bb32fb96fd/Monoid.zip
unzip -q Monoid.zip
# Powerline glyphs already integrated in this font.
cp Monoid-Regular.ttf $FONTDIR/Monoid.ttf

# Setup OpenDyslexic.ttf - http://opendyslexic.org/
cd $TMPDIR
curl -L -O https://github.com/antijingoist/open-dyslexic/raw/20160623-Stable/otf/OpenDyslexicMono-Regular.otf
$FONTPATCHER OpenDyslexicMono-Regular.otf
mv "OpenDyslexicMono Regular for Powerline.otf" $FONTDIR/OpenDyslexic.ttf

# Setup Ubuntu.ttf - http://font.ubuntu.com/
cd $TMPDIR
UBUNTU_VERSION=0.83
curl -L -O http://font.ubuntu.com/download/ubuntu-font-family-${UBUNTU_VERSION}.zip
unzip -o -q ubuntu-font-family-${UBUNTU_VERSION}.zip
$FONTPATCHER ubuntu-font-family-${UBUNTU_VERSION}/UbuntuMono-R.ttf
mv "Ubuntu Mono for Powerline.ttf" $FONTDIR/Ubuntu.ttf

# Setup Source-Code-Pro.ttf - https://github.com/adobe-fonts/source-code-pro
cd $TMPDIR
curl -L -O https://github.com/adobe-fonts/source-code-pro/archive/2.030R-ro/1.050R-it.zip
unzip -o -q 1.050R-it.zip
# Powerline glyphs already integrated in this font.
cp source-code-pro-2.030R-ro-1.050R-it/OTF/SourceCodePro-Regular.otf $FONTDIR/Source-Code-Pro.ttf
