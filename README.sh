#! /usr/bin/env sh

cat README.md \
    | sed '
        s_img/Showcase\_1.png_https://cdn.modrinth.com/data/Jjfssiel/images/f5a422c33a20dcab06013c391a5b43cc90e86f31.png_;
        s_https://github.com/un-nubivago/burning_https://modrinth.com/mod/burning_;
        s_https://github.com/un-nubivago/heater_https://modrinth.com/mod/heater_;
        s_https://github.com/wisp-forest/alloy-forgery_https://modrinth.com/mod/alloy-forgery_;'
