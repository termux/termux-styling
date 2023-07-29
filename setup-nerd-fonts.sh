#!/usr/bin/env bash
set -e -u

getNerdFont() {
	local font_name="${1}"
	local font_file="${2}"

	local tag="v3.0.2"
	local url="https://github.com/ryanoasis/nerd-fonts/raw/${tag}/patched-fonts/${font_name}/${font_file}"

	local local_file="app/src/main/assets/fonts/${font_name}.ttf"
	# Report which font is being downloaded, and keep a running count.
	echo "Fetching ${url} ... [$(( ++font_counter ))/${#fonts[@]}]"
	curl -fLo "${local_file}" "${url}"
}

declare -A fonts=( # [font-name]='path/to/file.ttf'
	[AnonymousPro]='Regular/AnonymiceProNerdFontMono-Regular.ttf'
	[DejaVuSansMono]='Regular/DejaVuSansMNerdFontMono-Regular.ttf'
	[FantasqueSansMono]='Regular/FantasqueSansMNerdFontMono-Regular.ttf'
	[FiraCode]='Regular/FiraCodeNerdFontMono-Regular.ttf'
	[FiraMono]='Regular/FiraMonoNerdFontMono-Regular.otf'
	[Go-Mono]='Regular/GoMonoNerdFontMono-Regular.ttf'
	[Hack]='Regular/HackNerdFontMono-Regular.ttf'
	[Hermit]='Regular/HurmitNerdFontMono-Regular.otf'
	[Inconsolata]='InconsolataNerdFontMono-Regular.ttf'
	[Iosevka]='Regular/IosevkaNerdFontMono-Regular.ttf'
	[LiberationMono]='LiterationMonoNerdFontMono-Regular.ttf'
	[Meslo]='L/Regular/MesloLGLNerdFontMono-Regular.ttf'
	[Monofur]='Regular/MonofurNerdFontMono-Regular.ttf'
	[Monoid]='Regular/MonoidNerdFontMono-Regular.ttf'
	[OpenDyslexic]='Mono-Regular/OpenDyslexicMNerdFontMono-Regular.otf'
	[RobotoMono]='Regular/RobotoMonoNerdFontMono-Regular.ttf'
	[SourceCodePro]='Regular/SauceCodeProNerdFontMono-Regular.ttf'
	[Terminus]='Regular/TerminessNerdFontMono-Regular.ttf'
	[UbuntuMono]='Regular/UbuntuMonoNerdFontMono-Regular.ttf'
	[VictorMono]='Regular/VictorMonoNerdFontMono-Regular.ttf'
)

# Starting log message
echo -e "\nDownloading ${#fonts[@]} NerdFonts from github.com/ryanoasis/nerd-fonts\n"

font_counter='0'
for font in "${!fonts[@]}"; do
	font_path="${fonts[${font}]}"
	getNerdFont "${font}" "${font_path}"
done

# Ending log message
echo -e "\nDownloaded ${font_counter}/${#fonts[*]} Fonts.\n"

unset 'fonts[@]' 'font' 'font_path' 'font_counter'
