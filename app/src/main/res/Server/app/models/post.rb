class Post < ActiveRecord::Base
    validates:location, presence:true
    validates:speed, presence:true
    validates:address, presence:true
    validates:latitude, presence:true
    validates:longitude, presence:true
end
