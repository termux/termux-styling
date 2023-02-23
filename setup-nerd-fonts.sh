#!/usr/bin/env bash
set -e -u

getNerdFont() {
	commit=12a523c32d55bdde88074e5b09e2b2e1eb9b5342
	url="https://github.com/ryanoasis/nerd-fonts/raw/$commit/patched-fonts/${2}"
	local_file=app/src/main/assets/fonts/$1.ttf
	echo "Fetching $url ..."
	curl -fLo "${local_file}" "${url}"
}

getNerdFont Anonymous-Pro \
	"AnonymousPro/complete/Anonymice%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont DejaVu \
	"DejaVuSansMono/Regular/complete/DejaVu%20Sans%20Mono%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Fantasque \
	"FantasqueSansMono/Regular/complete/Fantasque%20Sans%20Mono%20Regular%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont FiraCode \
	"FiraCode/Regular/complete/Fira%20Code%20Regular%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Fira \
	"FiraMono/Regular/complete/Fura%20Mono%20Regular%20Nerd%20Font%20Complete%20Mono.otf"

getNerdFont Go \
	"Go-Mono/Regular/complete/Go%20Mono%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Hack \
	"Hack/Regular/complete/Hack%20Regular%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Hermit \
	"Hermit/Medium/complete/Hurmit%20Medium%20Nerd%20Font%20Complete%20Mono.otf"

getNerdFont Inconsolata \
	"InconsolataLGC/Regular/complete/Inconsolata%20LGC%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Iosevka \
	"Iosevka/Regular/complete/Iosevka%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont LiberationMono \
	"LiberationMono/complete/Literation%20Mono%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Meslo \
	"Meslo/L/Regular/complete/Meslo%20LG%20L%20Regular%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Monofur \
	"Monofur/Regular/complete/monofur%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Monoid \
	"Monoid/Regular/complete/Monoid%20Regular%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont OpenDyslexic \
	"OpenDyslexic/Mono-Regular/complete/OpenDyslexicMono%20Regular%20Nerd%20Font%20Complete%20Mono.otf"

getNerdFont Roboto \
	"RobotoMono/Regular/complete/Roboto%20Mono%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Source-Code-Pro \
	"SourceCodePro/Regular/complete/Sauce%20Code%20Pro%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Terminus \
	"Terminus/terminus-ttf-4.40.1/Regular/complete/Terminess%20(TTF)%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont Ubuntu \
	"UbuntuMono/Regular/complete/Ubuntu%20Mono%20Nerd%20Font%20Complete%20Mono.ttf"

getNerdFont VictorMono \
	"VictorMono/Regular/complete/Victor%20Mono%20Regular%20Nerd%20Font%20Complete%20Mono.ttf"
