#!/usr/bin/env bash
set -e -u

getNerdFont() {
	local font_name="${1}"
	local font_file="${2}"

	local tag="v3.1.1"
	local font_pack="${font_name}"
	case "${font_pack}" in
		("Go-Mono")
			# These fonts have hyphens in the pack name.
			;;
		(*)
			# The rest don't.
			font_pack="${font_pack//-/}"
			;;
	esac
	local url="https://github.com/ryanoasis/nerd-fonts/releases/download/${tag}/${font_pack}.tar.xz"

	local local_file="app/src/main/assets/fonts/${font_name}.ttf"
	# Report which font is being downloaded, and keep a running count.
	echo "Fetching ${url} ... [$(( ++font_counter ))/${#fonts[@]}]"
	curl -fLs "${url}" | tar -xJO -f - "${font_file}" > "${local_file}"
	echo -e "\t-> Saved to ${local_file}"
}

declare -A fonts=( # [font-name]='path/to/file.ttf'
	[Anonymous-Pro]='AnonymiceProNerdFont-Regular.ttf'
	[DejaVu-Sans-Mono]='DejaVuSansMNerdFont-Regular.ttf'
	[Fantasque-Sans-Mono]='FantasqueSansMNerdFont-Regular.ttf'
	[Fira-Code]='FiraCodeNerdFont-Regular.ttf'
	[Fira-Mono]='FiraMonoNerdFont-Regular.otf'
	[Go-Mono]='GoMonoNerdFont-Regular.ttf'
	[Hack]='HackNerdFont-Regular.ttf'
	[Hermit]='HurmitNerdFont-Regular.otf'
	[Inconsolata]='InconsolataNerdFont-Regular.ttf'
	[Iosevka]='IosevkaNerdFont-Regular.ttf'
	[Liberation-Mono]='LiterationMonoNerdFont-Regular.ttf'
	[Meslo]='MesloLGLNerdFont-Regular.ttf'
	[Monofur]='MonofurNerdFont-Regular.ttf'
	[Monoid]='MonoidNerdFont-Regular.ttf'
	[OpenDyslexic]='OpenDyslexicMNerdFont-Regular.otf'
	[Roboto-Mono]='RobotoMonoNerdFont-Regular.ttf'
	[Source-Code-Pro]='SauceCodeProNerdFont-Regular.ttf'
	[Terminus]='TerminessNerdFont-Regular.ttf'
	[Ubuntu-Mono]='UbuntuMonoNerdFont-Regular.ttf'
	[Victor-Mono]='VictorMonoNerdFont-Regular.ttf'
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
