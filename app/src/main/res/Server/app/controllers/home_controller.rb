class HomeController < ApplicationController
    def ev_pos
        @posts = Post.all
    end

end
