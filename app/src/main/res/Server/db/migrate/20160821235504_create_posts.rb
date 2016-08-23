class CreatePosts < ActiveRecord::Migration
  def change
    create_table :posts do |t|

      t.text :location
      t.string :speed
      t.text :address
      t.float :latitude
      t.float :longitude
      
      t.timestamps null: false
    end
  end
end
